/*
 * org.openmicroscopy.shoola.agents.datamng.editors.DatasetImagesPane
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
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

package org.openmicroscopy.shoola.agents.datamng.editors.dataset;


//Java imports
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.datamng.DataManager;
import org.openmicroscopy.shoola.env.data.model.ImageSummary;
import org.openmicroscopy.shoola.util.ui.TableComponent;
import org.openmicroscopy.shoola.util.ui.TableComponentCellEditor;
import org.openmicroscopy.shoola.util.ui.TableComponentCellRenderer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
class DatasetImagesPane
	extends JPanel
{
	
	/** ID to position the components. */
	private static final int		POS_ONE = 0, POS_TWO = 1, POS_THREE = 2,
									POS_FOUR = 3;
	
	private static final int		ROW_HEIGHT = 25;
									
	/** Reference to the manager. */
	private DatasetEditorManager 	manager;
	
	private JButton					removeButton, resetButton;
	
	private JPanel					tablePanel, tableToAddPanel, buttonsPanel;
	
	private ImagesTableModel 		imagesTM;
	
	private List					imagesToAdd;
	
	private List					listImages;
	
	DatasetImagesPane(DatasetEditorManager manager)
	{
		this.manager = manager;
		imagesToAdd = new ArrayList();
		buildGUI();
	}

	/** Return the remove button. */
	JButton getRemoveButton() { return removeButton; }

	/** Return the cancel button. */
	JButton getResetButton() { return resetButton;}
	
	/** Select or not all images. */
	void setSelection(Object val)
	{
		int countCol = imagesTM.getColumnCount()-1;
		for (int i = 0; i < imagesTM.getRowCount(); i++)
			imagesTM.setValueAt(val, i, countCol);
	}
	
	/** Rebuild the component if some datasets are marked to be added. */ 
	void buildComponent(List l)
	{
		imagesToAdd = l;
		removeAll();
		tableToAddPanel = buildTableToAddPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tablePanel, POS_ONE);
		add(tableToAddPanel, POS_TWO);
		add(Box.createRigidArea(DataManager.VBOX), POS_THREE);
		add(buttonsPanel, POS_FOUR);
		Border b = BorderFactory.createEmptyBorder(0, 0, 10, 10);
		setBorder(b);
	}
	
	/** Build and lay out the GUI. */
	private void buildGUI()
	{
		listImages = manager.getDatasetData().getImages();
		tablePanel = buildTablePanel();
		buttonsPanel = buildButtonsPanel();
		tableToAddPanel = buildTableToAddPanel();
		JPanel p = new JPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tablePanel, POS_ONE);
		add(tableToAddPanel, POS_TWO);
		add(Box.createRigidArea(DataManager.VBOX), POS_THREE);
		add(buttonsPanel, POS_FOUR);
		Border b = BorderFactory.createEmptyBorder(0, 0, 10, 10);
		setBorder(b);
	}
	
	/** Initializes and build panel containing the buttons. */
	private JPanel buildButtonsPanel()
	{
		JPanel controls = new JPanel();
		//remove button
		removeButton = new JButton("Select All");
		removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeButton.setToolTipText(
			UIUtilities.formatToolTipText("Remove all datasets."));
		//cancel button
		resetButton = new JButton("Reset");
		resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		resetButton.setToolTipText(
			UIUtilities.formatToolTipText("Cancel selection."));
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		controls.add(resetButton);
		controls.add(Box.createRigidArea(DataManager.HBOX));
		controls.add(removeButton);
		controls.setOpaque(false); //make panel transparent
	
		if (listImages == null || listImages.size() == 0) {
			removeButton.setEnabled(false);
			resetButton.setEnabled(false);
		}
		return controls;
	}

	/** Build panel with table containing the images to add. */
	private JPanel buildTableToAddPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		if (imagesToAdd.size() != 0) {
			p.add(buildLabelTable());
	  		ImagesAddTableModel imagesAddTM = new ImagesAddTableModel();
	  		JTable table = new JTable(imagesAddTM);
	  		table.setBackground(DataManager.STEELBLUE);
	  		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  		table.setPreferredScrollableViewportSize(DataManager.VP_DIM);
	  		//wrap table in a scroll pane and add it to the panel
	  		JScrollPane spAdd = new JScrollPane(table);
			p.add(spAdd);
  		}
		return p;
	}
	
	/** Build panel with table containing existing datasets. */
	private JPanel buildTablePanel()
	{
  		JPanel  p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		//images table
		imagesTM = new ImagesTableModel();
		JTable t = new JTable(imagesTM);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setPreferredScrollableViewportSize(DataManager.VP_DIM);
		//wrap table in a scroll pane and add it to the panel
		JScrollPane sp = new JScrollPane(t);
		p.add(sp);
		return p;
	}
	
	private TableComponent buildLabelTable()
	{
		TableComponent table = new TableComponent(1, 3);
		setTableLayout(table);
		//First row.
		JLabel label = new JLabel(" Images to add");
		table.setValueAt(label, 0, 0);
		label = new JLabel("");
		table.setValueAt(label, 0, 1);
		table.setValueAt(label, 0, 2);
		return table;
	}

	/** Set the table layout. */
	private void setTableLayout(TableComponent table)
	{
		table.setTableHeader(null);
		table.setOpaque(false);
		table.setShowGrid(false);
		table.setRowHeight(ROW_HEIGHT);
		table.setDefaultRenderer(JComponent.class, 
								new TableComponentCellRenderer());
		table.setDefaultEditor(JComponent.class, 
								new TableComponentCellEditor());
	}
	
	/** 
	 * A <code>3</code>-column table model to view the summary of 
	 * images contained in the dataset.
	 * The first column contains the datasets ID and the 
	 * second column the names. Cells are not editable. 
	 */
	private class ImagesTableModel
		extends AbstractTableModel
	{
		private final String[]	columnNames = {"ID", "Name", "Select"};
		private final Object[]	images = listImages.toArray();
		private Object[][]		data = new Object[images.length][3];
		private Map 			imageSummaries;
		
		private ImagesTableModel()
		{
			imageSummaries = new HashMap();
			ImageSummary is;
			for (int i = 0; i < images.length; i++) {
				is = (ImageSummary) images[i];
				String sID = ""+ is.getID();
				data[i][0] = sID;
				data[i][1] = is.getName();
				data[i][2] = new Boolean(false);
				imageSummaries.put(sID, is);
			}
		}
	
		public int getColumnCount() { return 3; }
	
		public int getRowCount() { return images.length; }
	
		public String getColumnName(int col) { return columnNames[col]; }
	
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}
		
		public Object getValueAt(int row, int col) { return data[row][col]; }
		
		public boolean isCellEditable(int row, int col) 
		{ 
			boolean isEditable = false;
			if (col == 2) isEditable = true;
			return isEditable; 
		}
		
		public void setValueAt(Object value, int row, int col)
		{
			data[row][col]= value;
			fireTableCellUpdated(row, col);
			ImageSummary is = (ImageSummary) 
								imageSummaries.get((String) data[row][0]);
			manager.selectImage(((Boolean) value).booleanValue(), is);
		}
	}
	
	/** 
	 * A <code>3</code>-column table model to view the summary of 
	 * datasets to be added to the project.
	 * The first column contains the datasets ID and the 
	 * second column the names. Cells are not editable. 
	 */
	private class ImagesAddTableModel
		extends AbstractTableModel
	{
		private final String[]	columnNames = {"ID", "Name", "Remove"};
		private final Object[]	images = imagesToAdd.toArray();
		private Object[][] 		data = new Object[images.length][3];
		private Map 			imageSummaries;

		private ImagesAddTableModel()
		{
			imageSummaries = new HashMap();
			for (int i = 0; i < images.length; i++) {
				String sID = ""+ ((ImageSummary) images[i]).getID();
				data[i][0] = sID;
				data[i][1] = ((ImageSummary) images[i]).getName();
				data[i][2] = new Boolean(false);
				imageSummaries.put(sID, images[i]);
			}
		}

		public int getColumnCount() { return 3; }

		public int getRowCount() { return images.length; }

		public String getColumnName(int col) { return columnNames[col]; }

		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		public Object getValueAt(int row, int col) { return data[row][col]; }

		public boolean isCellEditable(int row, int col)
		{ 
			boolean isEditable = false;
			if (col == 2) isEditable = true;
			return isEditable;
		}

		public void setValueAt(Object value, int row, int col)
		{
			data[row][col]= value;
			fireTableCellUpdated(row, col);
			ImageSummary is = (ImageSummary) 
								imageSummaries.get((String) data[row][0]);
			manager.updateAddSelection(((Boolean) value).booleanValue(), is);
		}
	}
	
}
