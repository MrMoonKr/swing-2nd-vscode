
/** 
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev. 
 *  All Rights Reserved. 
 * 
 *  =================================================== 
 *  This program contains code from the book "Swing" 
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev 
 *  http://www.spindoczine.com/sbe 
 *  =================================================== 
 * 
 *  The above paragraph must be included in full, unmodified 
 *  and completely intact in the beginning of any source code 
 *  file that references, copies or uses (in any way, shape 
 *  or form) code contained in this file. 
 */
package com.example;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class DirTree2 extends JFrame {

    public static final String APP_NAME = "Directories Tree";

    public static final ImageIcon ICON_COMPUTER = new ImageIcon( "computer.gif" );
    public static final ImageIcon ICON_DISK = new ImageIcon( "disk.gif" );
    public static final ImageIcon ICON_FOLDER = new ImageIcon( "folder.gif" );
    public static final ImageIcon ICON_EXPANDEDFOLDER = new ImageIcon( "expandedfolder.gif" );

    protected JTree m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;

    // NEW
    protected DefaultTreeCellEditor m_editor;
    protected FileNode m_editingNode;
    protected JPopupMenu m_popup;
    protected Action m_expandAction;
    protected TreePath m_clickedPath;

    public DirTree2() {
        super( APP_NAME );
        setSize( 400, 300 );

        DefaultMutableTreeNode top = new DefaultMutableTreeNode( new IconData( ICON_COMPUTER, null, "Computer" ) );

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for ( int k = 0; k < roots.length; k++ ) {
            node = new DefaultMutableTreeNode( new IconData( ICON_DISK, null, new FileNode( roots[k] ) ) );
            top.add( node );
            node.add( new DefaultMutableTreeNode( new Boolean( true ) ) );
        }

        m_model = new DefaultTreeModel( top );
        m_tree = new JTree( m_model ) { // NEW
            public boolean isPathEditable( TreePath path ) {
                if ( path == null || path.getPathCount() < 3 ) // Skip first two levels
                    return false;
                FileNode node = getFileNode( getTreeNode( path ) );
                if ( node == null )
                    return false;
                File dir = node.getFile();
                if ( dir != null && dir.isDirectory() ) {
                    m_editingNode = node;
                    return true;
                }
                return false;
            }
        };

        m_tree.putClientProperty( "JTree.lineStyle", "Angled" );

        IconCellRenderer renderer = new IconCellRenderer();
        m_tree.setCellRenderer( renderer );

        m_tree.addTreeExpansionListener( new DirExpansionListener() );

        m_tree.addTreeSelectionListener( new DirSelectionListener() );

        m_tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        m_tree.setShowsRootHandles( true );

        JScrollPane s = new JScrollPane();
        s.getViewport().add( m_tree );
        getContentPane().add( s, BorderLayout.CENTER );

        // NEW
        CellEditorListener cel = new CellEditorListener() {
            public void editingStopped( ChangeEvent e ) {
                if ( m_editingNode != null ) {
                    String newName = m_editor.getCellEditorValue().toString();
                    File dir = m_editingNode.getFile();
                    File newDir = new File( dir.getParentFile(), newName );
                    dir.renameTo( newDir );
                    // Update tree
                    TreePath path = m_tree.getSelectionPath();
                    DefaultMutableTreeNode node = getTreeNode( path );
                    IconData idata = new IconData( DirTree2.ICON_FOLDER, DirTree2.ICON_EXPANDEDFOLDER,
                            new FileNode( newDir ) );
                    node.setUserObject( idata );
                    m_model.nodeStructureChanged( node );
                    m_display.setText( newDir.getAbsolutePath() );
                }
                m_editingNode = null;
            }

            public void editingCanceled( ChangeEvent e ) {
                m_editingNode = null;
            }
        };
        m_editor = new DefaultTreeCellEditor( m_tree, renderer );
        m_editor.addCellEditorListener( cel );
        m_tree.setCellEditor( m_editor );
        m_tree.setEditable( true );

        // NEW
        JToolBar tb = new JToolBar();
        tb.setFloatable( false );
        m_display = new JTextField();
        m_display.setEditable( false );
        m_display.setBorder( new SoftBevelBorder( BevelBorder.LOWERED ) );
        tb.add( m_display );
        tb.addSeparator();

        // NEW
        m_popup = new JPopupMenu();
        m_expandAction = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if ( m_clickedPath == null )
                    return;
                if ( m_tree.isExpanded( m_clickedPath ) )
                    m_tree.collapsePath( m_clickedPath );
                else
                    m_tree.expandPath( m_clickedPath );
            }
        };
        m_popup.add( m_expandAction );
        m_popup.addSeparator();

        Action a1 = new AbstractAction( "Create", new ImageIcon( "New16.gif" ) ) {
            public void actionPerformed( ActionEvent e ) {
                m_tree.repaint();
                TreePath path = m_tree.getSelectionPath();
                if ( path == null || path.getPathCount() < 2 )
                    return;
                DefaultMutableTreeNode treeNode = getTreeNode( path );
                FileNode node = getFileNode( treeNode );
                if ( node == null )
                    return;

                File dir = node.getFile();
                int index = 0;
                File newDir = new File( dir, "New Directory" );
                while ( newDir.exists() ) {
                    index++;
                    newDir = new File( dir, "New Directory" + index );
                }
                newDir.mkdirs();

                IconData idata = new IconData( DirTree2.ICON_FOLDER, DirTree2.ICON_EXPANDEDFOLDER,
                        new FileNode( newDir ) );
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( idata );
                treeNode.add( newNode );
                m_model.nodeStructureChanged( treeNode );

                path = path.pathByAddingChild( newNode );
                m_tree.scrollPathToVisible( path );
                m_tree.startEditingAtPath( path );
            }
        };
        m_popup.add( a1 );
        JButton bt = tb.add( a1 );
        bt.setToolTipText( "Create new directory" );

        Action a2 = new AbstractAction( "Delete", new ImageIcon( "Delete16.gif" ) ) {
            public void actionPerformed( ActionEvent e ) {
                m_tree.repaint();
                TreePath path = m_tree.getSelectionPath();
                if ( path == null || path.getPathCount() < 3 )
                    return;
                DefaultMutableTreeNode treeNode = getTreeNode( path );
                FileNode node = getFileNode( treeNode );
                if ( node == null )
                    return;
                File dir = node.getFile();
                if ( dir != null && dir.isDirectory() ) {
                    if ( JOptionPane.showConfirmDialog( DirTree2.this,
                            "Do you want to delete \ndirectory \"" + dir.getName() + "\" ?", DirTree2.APP_NAME,
                            JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION )
                        return;

                    setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                    deleteDirectory( dir );
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );

                    TreeNode parent = treeNode.getParent();
                    treeNode.removeFromParent();
                    m_model.nodeStructureChanged( parent );
                    m_display.setText( "" );
                }
            }
        };
        m_popup.add( a2 );
        bt = tb.add( a2 );
        bt.setToolTipText( "Delete directory" );
        m_tree.registerKeyboardAction( a2, KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ), JComponent.WHEN_FOCUSED );

        Action a3 = new AbstractAction( "Rename", new ImageIcon( "Edit16.gif" ) ) {
            public void actionPerformed( ActionEvent e ) {
                m_tree.repaint();
                TreePath path = m_tree.getSelectionPath();
                if ( path == null )
                    return;
                m_tree.scrollPathToVisible( path );
                m_tree.startEditingAtPath( path );
            }
        };
        m_popup.add( a3 );
        bt = tb.add( a3 );
        bt.setToolTipText( "Rename directory" );

        getContentPane().add( tb, BorderLayout.NORTH );
        m_tree.add( m_popup );
        m_tree.addMouseListener( new PopupTrigger() );
    }

    DefaultMutableTreeNode getTreeNode( TreePath path ) {
        return ( DefaultMutableTreeNode )( path.getLastPathComponent() );
    }

    FileNode getFileNode( DefaultMutableTreeNode node ) {
        if ( node == null )
            return null;
        Object obj = node.getUserObject();
        if ( obj instanceof IconData )
            obj = ( ( IconData )obj ).getObject();
        if ( obj instanceof FileNode )
            return ( FileNode )obj;
        else
            return null;
    }

    // NEW
    class PopupTrigger extends MouseAdapter {

        public void mouseReleased( MouseEvent e ) {
            if ( e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3 ) {
                int x = e.getX();
                int y = e.getY();
                TreePath path = m_tree.getPathForLocation( x, y );
                if ( path == null )
                    return;

                if ( m_tree.isExpanded( path ) )
                    m_expandAction.putValue( Action.NAME, "Collapse" );
                else
                    m_expandAction.putValue( Action.NAME, "Expand" );

                m_tree.setSelectionPath( path );
                m_tree.scrollPathToVisible( path );
                m_popup.show( m_tree, x, y );
                m_clickedPath = path;
            }
        }
    }

    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    class DirExpansionListener implements TreeExpansionListener {

        public void treeExpanded( TreeExpansionEvent event ) {

            final DefaultMutableTreeNode node = getTreeNode( event.getPath() );
            final FileNode fnode = getFileNode( node );

            Thread runner = new Thread() {
                public void run() {
                    if ( fnode != null && fnode.expand( node ) ) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                m_model.reload( node );
                            }
                        };
                        SwingUtilities.invokeLater( runnable );
                    }
                }
            };
            runner.start();
        }

        public void treeCollapsed( TreeExpansionEvent event ) {
        }
    }

    class DirSelectionListener implements TreeSelectionListener {

        public void valueChanged( TreeSelectionEvent event ) {
            DefaultMutableTreeNode node = getTreeNode( event.getPath() );
            FileNode fnode = getFileNode( node );
            if ( fnode != null )
                m_display.setText( fnode.getFile().getAbsolutePath() );
            else
                m_display.setText( "" );
        }
    }

    // NEW
    public static void deleteDirectory( File dir ) {
        if ( dir == null || !dir.isDirectory() || dir.isHidden() )
            return;
        File[] files = dir.listFiles();
        if ( files != null )
            for ( int k = 0; k < files.length; k++ ) {
                File f = files[k];
                if ( f.isDirectory() )
                    deleteDirectory( f );
                else
                    f.delete();
            }
        dir.delete();
    }

    public static void main( String argv[] ) {
        DirTree2 frame = new DirTree2();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

class IconCellRenderer extends DefaultTreeCellRenderer {

    public IconCellRenderer() {
        setLeafIcon( null );
        setOpenIcon( null );
    }

    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus ) {

        // Invoke default implementation
        Component result = super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

        DefaultMutableTreeNode node = ( DefaultMutableTreeNode )value;
        Object obj = node.getUserObject();
        setText( obj.toString() );

        if ( obj instanceof Boolean )
            setText( "Retrieving data..." );

        if ( obj instanceof IconData ) {
            IconData idata = ( IconData )obj;
            if ( expanded )
                setIcon( idata.getExpandedIcon() );
            else
                setIcon( idata.getIcon() );
        } else
            setIcon( null );

        return result;
    }
}

class IconData {
    protected Icon m_icon;
    protected Icon m_expandedIcon;
    protected Object m_data;

    public IconData( Icon icon, Object data ) {
        m_icon = icon;
        m_expandedIcon = null;
        m_data = data;
    }

    public IconData( Icon icon, Icon expandedIcon, Object data ) {
        m_icon = icon;
        m_expandedIcon = expandedIcon;
        m_data = data;
    }

    public Icon getIcon() {
        return m_icon;
    }

    public Icon getExpandedIcon() {
        return m_expandedIcon != null ? m_expandedIcon : m_icon;
    }

    public Object getObject() {
        return m_data;
    }

    public String toString() {
        return m_data.toString();
    }
}

class FileNode {
    protected File m_file;

    public FileNode( File file ) {
        m_file = file;
    }

    public File getFile() {
        return m_file;
    }

    public String toString() {
        return m_file.getName().length() > 0 ? m_file.getName() : m_file.getPath();
    }

    // Alternatively we copud sub-class TreeNode
    public boolean expand( DefaultMutableTreeNode parent ) {
        DefaultMutableTreeNode flag = ( DefaultMutableTreeNode )parent.getFirstChild();
        if ( flag == null ) // No flag
            return false;
        Object obj = flag.getUserObject();
        if ( !( obj instanceof Boolean ) )
            return false; // Already expanded

        parent.removeAllChildren(); // Remove Flag

        File[] files = listFiles();
        if ( files == null )
            return true;

        Vector v = new Vector();

        for ( int k = 0; k < files.length; k++ ) {
            File f = files[k];
            if ( !( f.isDirectory() ) )
                continue;

            FileNode newNode = new FileNode( f );

            boolean isAdded = false;
            for ( int i = 0; i < v.size(); i++ ) {
                FileNode nd = ( FileNode )v.elementAt( i );
                if ( newNode.compareTo( nd ) < 0 ) {
                    v.insertElementAt( newNode, i );
                    isAdded = true;
                    break;
                }
            }
            if ( !isAdded )
                v.addElement( newNode );
        }

        for ( int i = 0; i < v.size(); i++ ) {
            FileNode nd = ( FileNode )v.elementAt( i );
            IconData idata = new IconData( DirTree2.ICON_FOLDER, DirTree2.ICON_EXPANDEDFOLDER, nd );
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( idata );
            parent.add( node );

            if ( nd.hasSubDirs() )
                node.add( new DefaultMutableTreeNode( new Boolean( true ) ) );
        }

        return true;
    }

    public boolean hasSubDirs() {
        File[] files = listFiles();
        if ( files == null )
            return false;
        for ( int k = 0; k < files.length; k++ ) {
            if ( files[k].isDirectory() )
                return true;
        }
        return false;
    }

    public int compareTo( FileNode toCompare ) {
        return m_file.getName().compareToIgnoreCase( toCompare.m_file.getName() );
    }

    protected File[] listFiles() {
        if ( !m_file.isDirectory() )
            return null;
        try {
            return m_file.listFiles();
        } catch ( Exception ex ) {
            JOptionPane.showMessageDialog( null, "Error reading directory " + m_file.getAbsolutePath(),
                    DirTree2.APP_NAME, JOptionPane.WARNING_MESSAGE );
            return null;
        }
    }
}
