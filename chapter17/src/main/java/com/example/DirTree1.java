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
//import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class DirTree1 extends JFrame {

    public static final String APP_NAME = "Directories Tree";

    public static final ImageIcon ICON_COMPUTER = IconLoader.loadIcon( "icons/computer.gif" );
    public static final ImageIcon ICON_DISK = IconLoader.loadIcon( "icons/disk.gif" );
    public static final ImageIcon ICON_FOLDER = IconLoader.loadIcon( "icons/folder.gif" );
    public static final ImageIcon ICON_EXPANDEDFOLDER = IconLoader.loadIcon( "icons/expandedfolder.gif" );

    protected JTree m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;

    public DirTree1() {
        super( APP_NAME );
        setSize( 400, 300 );

        DefaultMutableTreeNode top = new DefaultMutableTreeNode( new IconData( ICON_COMPUTER, null, "Computer" ) );

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for ( int k = 0; k < roots.length; k++ ) {
            node = new DefaultMutableTreeNode( new IconData( ICON_DISK, null, new FileNode( roots[k] ) ) );
            top.add( node );
            node.add( new DefaultMutableTreeNode( Boolean.valueOf( true ) ) );
        }

        m_model = new DefaultTreeModel( top );
        m_tree = new JTree( m_model );

        m_tree.putClientProperty( "JTree.lineStyle", "Angled" );

        IconCellRenderer renderer = new IconCellRenderer();
        m_tree.setCellRenderer( renderer );

        m_tree.addTreeExpansionListener( new DirExpansionListener() );

        m_tree.addTreeSelectionListener( new DirSelectionListener() );

        m_tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        m_tree.setShowsRootHandles( true );
        m_tree.setEditable( false );

        JScrollPane s = new JScrollPane();
        s.getViewport().add( m_tree );
        getContentPane().add( s, BorderLayout.CENTER );

        m_display = new JTextField();
        m_display.setEditable( false );
        m_display.setBorder( new SoftBevelBorder( BevelBorder.LOWERED ) );
        getContentPane().add( m_display, BorderLayout.NORTH );
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

    public static void main( String argv[] ) {
        DirTree1 frame = new DirTree1();
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

        Vector<FileNode> v = new Vector<>();

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
            IconData idata = new IconData( DirTree1.ICON_FOLDER, DirTree1.ICON_EXPANDEDFOLDER, nd );
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( idata );
            parent.add( node );

            if ( nd.hasSubDirs() )
                node.add( new DefaultMutableTreeNode( Boolean.valueOf( true ) ) );
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
                    DirTree1.APP_NAME, JOptionPane.WARNING_MESSAGE );
            return null;
        }
    }
}
