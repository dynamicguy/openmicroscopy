/*
 * Copyright (C) 2014 University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package omero.cmd.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import ome.model.internal.Permissions;
import ome.model.meta.ExperimenterGroup;
import ome.security.ACLVoter;
import ome.security.SystemTypes;
import ome.services.delete.Deletion;
import ome.services.graphs.GraphException;
import ome.services.graphs.GraphPathBean;
import ome.services.graphs.GraphPolicy;
import ome.services.graphs.GraphTraversal;
import ome.system.EventContext;
import ome.system.Login;
import omero.cmd.Chgrp2;
import omero.cmd.Chgrp2Response;
import omero.cmd.HandleI.Cancel;
import omero.cmd.ERR;
import omero.cmd.Helper;
import omero.cmd.IRequest;
import omero.cmd.Response;

/**
 * Request to move model objects to a different experiment group, reimplementing {@link ChgrpI}.
 * @author m.t.b.carroll@dundee.ac.uk
 * @since 5.1.0
 */
public class Chgrp2I extends Chgrp2 implements IRequest, WrappableRequest<Chgrp2> {

    private static final ImmutableMap<String, String> ALL_GROUPS_CONTEXT = ImmutableMap.of(Login.OMERO_GROUP, "-1");

    private static final Set<GraphPolicy.Ability> REQUIRED_ABILITIES = ImmutableSet.of(GraphPolicy.Ability.OWN);

    private final ACLVoter aclVoter;
    private final SystemTypes systemTypes;
    private final GraphPathBean graphPathBean;
    private final Deletion deletionInstance;
    private GraphPolicy graphPolicy;  /* not final because of adjustGraphPolicy */
    private final SetMultimap<String, String> unnullable;

    private List<Function<GraphPolicy, GraphPolicy>> graphPolicyAdjusters = new ArrayList<Function<GraphPolicy, GraphPolicy>>();
    private Helper helper;
    private GraphTraversal graphTraversal;

    int targetObjectCount = 0;
    int deletedObjectCount = 0;
    int movedObjectCount = 0;

    /**
     * Construct a new <q>chgrp</q> request; called from {@link GraphRequestFactory#getRequest(Class)}.
     * @param aclVoter ACL voter for permissions checking
     * @param systemTypes for identifying the system types
     * @param graphPathBean the graph path bean to use
     * @param deletionInstance a deletion instance for deleting files
     * @param graphPolicy the graph policy to apply for chgrp
     * @param unnullable properties that, while nullable, may not be nulled by a graph traversal operation
     */
    public Chgrp2I(ACLVoter aclVoter, SystemTypes systemTypes, GraphPathBean graphPathBean, Deletion deletionInstance,
            GraphPolicy graphPolicy, SetMultimap<String, String> unnullable) {
        this.aclVoter = aclVoter;
        this.systemTypes = systemTypes;
        this.graphPathBean = graphPathBean;
        this.deletionInstance = deletionInstance;
        this.graphPolicy = graphPolicy;
        this.unnullable = unnullable;
    }

    @Override
    public Map<String, String> getCallContext() {
        return new HashMap<String, String>(ALL_GROUPS_CONTEXT);
    }

    @Override
    public void init(Helper helper) {
        this.helper = helper;
        helper.setSteps(dryRun ? 1 : 3);

        /* check that the user is a member of the destination group */
        final EventContext eventContext = helper.getEventContext();
        if (!(eventContext.isCurrentUserAdmin() || eventContext.getMemberOfGroupsList().contains(groupId))) {
            throw helper.cancel(new ERR(), new IllegalArgumentException(), "not a member of the chgrp destination group");
        }

        final ChildOptionI[] childOptions = ChildOptionI.castChildOptions(this.childOptions);

        if (childOptions != null) {
            for (final ChildOptionI childOption : childOptions) {
                childOption.init();
            }
        }

        final ExperimenterGroup destinationGroup = (ExperimenterGroup) helper.getSession().get(ExperimenterGroup.class, groupId);
        final Permissions destinationGroupPermissions = destinationGroup.getDetails().getPermissions();
        final boolean isToGroupReadable = destinationGroupPermissions.isGranted(Permissions.Role.GROUP, Permissions.Right.READ);

        if (!isToGroupReadable) {
            graphPolicy.setCondition("to_private");
        }

        GraphPolicy graphPolicyWithOptions = graphPolicy;

        graphPolicyWithOptions = ChildOptionsPolicy.getChildOptionsPolicy(graphPolicyWithOptions, graphPathBean, childOptions,
                REQUIRED_ABILITIES);

        for (final Function<GraphPolicy, GraphPolicy> adjuster : graphPolicyAdjusters) {
            graphPolicyWithOptions = adjuster.apply(graphPolicyWithOptions);
        }
        graphPolicyAdjusters = null;

        graphTraversal = new GraphTraversal(helper.getSession(), eventContext, aclVoter, systemTypes, graphPathBean, unnullable,
                graphPolicyWithOptions, dryRun ? new NullGraphTraversalProcessor(REQUIRED_ABILITIES) : new InternalProcessor());
    }

    @Override
    public Object step(int step) throws Cancel {
        helper.assertStep(step);
        try {
            switch (step) {
            case 0:
                /* if targetObjects were an IObjectList then this would need IceMapper.reverse */
                final SetMultimap<String, Long> targetMultimap = HashMultimap.create();
                for (final Entry<String, long[]> oneClassToTarget : targetObjects.entrySet()) {
                    String className = oneClassToTarget.getKey();
                    if (className.lastIndexOf('.') < 0) {
                        className = graphPathBean.getClassForSimpleName(className).getName();
                    }
                    for (final long id : oneClassToTarget.getValue()) {
                        targetMultimap.put(className, id);
                        targetObjectCount++;
                    }
                }
                return graphTraversal.planOperation(helper.getSession(), targetMultimap, true);
            case 1:
                graphTraversal.unlinkTargets();
                return null;
            case 2:
                graphTraversal.processTargets();
                return null;
            default:
                throw helper.cancel(new ERR(), new IllegalArgumentException(), "model object graph operation has no step " + step);
            }
        } catch (GraphException ge) {
            final omero.cmd.GraphException graphERR = new omero.cmd.GraphException();
            graphERR.message = ge.message;
            throw helper.cancel(graphERR, ge, "model object graph operation failed");
        } catch (Throwable t) {
            throw helper.cancel(new ERR(), t, "model object graph operation failed");
        }
    }

    @Override
    public void finish() {
    }

    @Override
    public void buildResponse(int step, Object object) {
        helper.assertResponse(step);
        if (step == 0) {
            /* if the results object were in terms of IObjectList then this would need IceMapper.map */
            final Entry<SetMultimap<String, Long>, SetMultimap<String, Long>> result =
                    (Entry<SetMultimap<String, Long>, SetMultimap<String, Long>>) object;
            if (!dryRun) {
                try {
                    deletionInstance.deleteFiles(GraphUtil.trimPackageNames(result.getValue()));
                } catch (Exception e) {
                    helper.cancel(new ERR(), e, "file deletion error");
                }
            }
            final Map<String, long[]> movedObjects = new HashMap<String, long[]>();
            final Map<String, long[]> deletedObjects = new HashMap<String, long[]>();
            for (final Entry<String, Collection<Long>> oneMovedClass : result.getKey().asMap().entrySet()) {
                final String className = oneMovedClass.getKey();
                final Collection<Long> ids = oneMovedClass.getValue();
                final long[] idArray = GraphUtil.idsToArray(ids);
                movedObjectCount += idArray.length;
                movedObjects.put(className, idArray);
            }
            for (final Entry<String, Collection<Long>> oneDeletedClass : result.getValue().asMap().entrySet()) {
                final String className = oneDeletedClass.getKey();
                final Collection<Long> ids = oneDeletedClass.getValue();
                final long[] idArray = GraphUtil.idsToArray(ids);
                deletedObjectCount += idArray.length;
                deletedObjects.put(className, idArray);
            }
            final Chgrp2Response response = new Chgrp2Response(movedObjects, deletedObjects);
            helper.setResponseIfNull(response);
            helper.info("in " + (dryRun ? "mock " : "") + "chgrp to " + groupId + " of " + targetObjectCount +
                    ", moved " + movedObjectCount + " and deleted " + deletedObjectCount + " in total");
        }
    }

    @Override
    public Response getResponse() {
        return helper.getResponse();
    }

    @Override
    public void copyFieldsTo(Chgrp2 request) {
        GraphUtil.copyFields(this, request);
        request.groupId = groupId;
    }

    @Override
    public void adjustGraphPolicy(Function<GraphPolicy, GraphPolicy> adjuster) {
        if (graphPolicyAdjusters == null) {
            throw new IllegalStateException("request is already initialized");
        } else {
            graphPolicyAdjusters.add(adjuster);
        }
    }

    @Override
    public GraphPolicy.Action getActionForStarting() {
        return GraphPolicy.Action.INCLUDE;
    }

    @Override
    public Map<String, long[]> getStartFrom(Response response) {
        return ((Chgrp2Response) response).includedObjects;
    }

    /**
     * A <q>chgrp</q> processor that updates model objects' group.
     * @author m.t.b.carroll@dundee.ac.uk
     * @since 5.1.0
     */
    private final class InternalProcessor extends BaseGraphTraversalProcessor {

        private final Logger LOGGER = LoggerFactory.getLogger(InternalProcessor.class);

        private final ExperimenterGroup group = new ExperimenterGroup(groupId, false);

        public InternalProcessor() {
            super(helper.getSession());
        }

        @Override
        public void processInstances(String className, Collection<Long> ids) throws GraphException {
            final String update = "UPDATE " + className + " SET details.group = :group WHERE id IN (:ids)";
            final int count =
                    session.createQuery(update).setParameter("group", group).setParameterList("ids", ids).executeUpdate();
            if (count != ids.size()) {
                LOGGER.warn("not all the objects of type " + className + " could be processed");
            }
        }

        @Override
        public Set<GraphPolicy.Ability> getRequiredPermissions() {
            return REQUIRED_ABILITIES;
        }
    }
}
