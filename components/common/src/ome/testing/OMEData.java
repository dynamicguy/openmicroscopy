/*
 * ome.testing
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package ome.testing;

//Java imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.sql.DataSource;

//Third-party libraries
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

//Application-internal dependencies

/** 
 * abstract data container for testing. Sub-classes can set whatever values
 * it would like in <code>init()</code>. After the OMEData instance is inserted into 
 * the test class by Spring, it SHOULD not be changed, but this is a matter
 * of opionon. Setting the same <code>seed</code> value for two independent
 * Data instances is also assumed to create identical values. 
 * 
 * @author  Josh Moore &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:josh.moore@gmx.de">josh.moore@gmx.de</a>
 * @version 1.0 
 * <small>
 * (<b>Internal version:</b> $Rev$ $Date$)
 * </small>
 * @since 1.0
 */
public class OMEData {


    final static String emptyColl = "Collections may not be empty.\n" +
            "You are currently trying to run a test on an OME database\n" +
            "that does not appear to have the needed data.\n" +
            "\n" +
            "There must be at least one:\n" +
            "project,dataset,image,experimenter,classification,category,category group,image annotation and dataset annotation\n" +
            "\n" +
            "Testing results would be unpredictable without test data.\n" +
            "Please fill your database and retry.";
    
    private static Log log = LogFactory.getLog(OMEData.class);
    
    boolean initialized = false;

    DataSource ds;

    Map properties;
    
    Map values = new HashMap();
    
    long seed;
    
    Random rnd;

    String[] files = new String[]{"test_data.properties"};
    
    public void setDataSource(DataSource dataSource) {
        this.ds = dataSource;
    }

    public OMEData() {
        init();
    }
    
    public OMEData(String[] files) {
        this.files = files;
        init();
    }
    
    void init(){
        properties = SqlPropertiesParser.parse(files);
        seed = (new Random()).nextLong();
        rnd = new Random(seed); 
    }
    
    /* allows for storing arbitrary objects in data */
    public void put(String propertyKey, Object value){
        toCache(propertyKey,value);
    }
    
    public List get(String propertyKey){
        
        if (inCache(propertyKey)){
            return (List) fromCache(propertyKey);
        }
        
        Object obj = properties.get(propertyKey);
        if (obj == null) {
            return null;
        } else if (obj instanceof List)
        {
            toCache(propertyKey,obj);
            return (List) obj;
        } else if (obj instanceof String){
            String sql = (String) obj;
            List result = runSql(sql);
            toCache(propertyKey,result);
            return result;
        } else {
            throw new RuntimeException("Error in properties. Not expecting "+obj==null ? null : obj.getClass().getName());
        }
    }

    List getRandomNumber(List l, Number number){
        
        if (number == null)
            return null;

        if (l == null || l.size() == 0) {
            log.warn(emptyColl);
            return null;
        }
        
        List ordered = new ArrayList(l);
        List result = new ArrayList();
       
        while (ordered.size() >0 && result.size() < number.longValue()) {
            int choice = randomChoice(ordered.size());
            result.add(ordered.remove(choice));
        }

        return result;

    }
    
    public List getMax(String propertyKey, int maximum){
        List l = get(propertyKey);
        return getRandomNumber(l,new Integer(maximum));
       
        
    }
    
    public List getPercent(String propertyKey, double percent){
        List l = get(propertyKey);
        return getRandomNumber(l,new Double(l.size()*percent));

    }
    
    public Object getRandom(String propertyKey){
        List l = get(propertyKey);
        List result = getRandomNumber(l,new Integer(1));
 
        if (result == null || result.size() < 1)
            return null;
        
        return result.get(0);
     }
    
    public Object getFirst(String propertyKey){
        List l = get(propertyKey);
        
        if (l == null || l.size() == 0) {
            log.warn(emptyColl);
            return null;
        }
        
        return l.get(0);
    }
    
    boolean inCache(String key){
        return values.containsKey(key);
    }
    
    void toCache(String key, Object value){
        values.put(key,value);
    }
    
    Object fromCache(String key){
        return values.get(key);
    }
    
    List runSql(String sql){
        JdbcTemplate jt = new JdbcTemplate(ds);
        SqlRowSet rows = jt.queryForRowSet(sql);
        List result = new ArrayList();
        while (rows.next()){
            result.add(rows.getObject(1));
        }
        log.debug("SQL:"+sql+"\n\nResult:"+result);
        return result;
    }

    public int randomChoice(int size) {
        double value = (size - 1) * rnd.nextDouble();
        return (new Double(value)).intValue();
    }
   
    public String toString(){
        ToStringBuilder tsb = new ToStringBuilder(this); 
        for (Iterator it = values.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();
            tsb.append(key,values.get(key));
        }
        return tsb.toString();
	}
}
