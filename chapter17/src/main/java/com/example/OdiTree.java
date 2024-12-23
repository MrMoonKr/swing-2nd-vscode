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
//import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class OdiTree extends JFrame {

    protected JTree m_tree = null;
    protected DefaultTreeModel m_model = null;
    protected JTextField m_display;

    public OdiTree() {
        super( "ODI Tree" );
        setSize( 400, 300 );

        Object[] nodes = new Object[5];
        DefaultMutableTreeNode top = new DefaultMutableTreeNode( new OidNode( 1, "ISO" ) );
        DefaultMutableTreeNode parent = top;
        nodes[0] = top;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode( new OidNode( 0, "standard" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 2, "member-body" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 3, "org" ) );
        parent.add( node );
        parent = node;
        nodes[1] = parent;

        node = new DefaultMutableTreeNode( new OidNode( 6, "dod" ) );
        parent.add( node );
        parent = node;
        nodes[2] = parent;

        node = new DefaultMutableTreeNode( new OidNode( 1, "internet" ) );
        parent.add( node );
        parent = node;
        nodes[3] = parent;

        node = new DefaultMutableTreeNode( new OidNode( 1, "directory" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 2, "mgmt" ) );
        parent.add( node );
        nodes[4] = node;
        node.add( new DefaultMutableTreeNode( new OidNode( 1, "mib-2" ) ) );
        node = new DefaultMutableTreeNode( new OidNode( 3, "experimental" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 4, "private" ) );
        node.add( new DefaultMutableTreeNode( new OidNode( 1, "enterprises" ) ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 5, "security" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 6, "snmpV2" ) );
        parent.add( node );
        node = new DefaultMutableTreeNode( new OidNode( 7, "mail" ) );
        parent.add( node );

        m_model = new DefaultTreeModel( top );
        m_tree = new JTree( m_model );

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon( new ImageIcon( "opened.gif" ) );
        renderer.setClosedIcon( new ImageIcon( "closed.gif" ) );
        renderer.setLeafIcon( new ImageIcon( "leaf.gif" ) );
        m_tree.setCellRenderer( renderer );
        m_tree.setShowsRootHandles( true );
        m_tree.setEditable( false );

        m_tree.addTreeSelectionListener( new OidSelectionListener() );

        JScrollPane s = new JScrollPane();
        s.getViewport().add( m_tree );
        getContentPane().add( s, BorderLayout.CENTER );

        m_display = new JTextField(); // Use JTextField to allow copy operation
        m_display.setEditable( false );
        m_display.setBorder( new SoftBevelBorder( BevelBorder.LOWERED ) );
        getContentPane().add( m_display, BorderLayout.SOUTH );

        TreePath path = new TreePath( nodes );
        m_tree.setSelectionPath( path );
    }

    public static void main( String argv[] ) {
        OdiTree frame = new OdiTree();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

    class OidSelectionListener implements TreeSelectionListener {

        public void valueChanged( TreeSelectionEvent e ) {
            TreePath path = e.getPath();
            Object[] nodes = path.getPath();
            String oid = "";
            for ( int k = 0; k < nodes.length; k++ ) {
                DefaultMutableTreeNode node = ( DefaultMutableTreeNode )nodes[k];
                OidNode nd = ( OidNode )node.getUserObject();
                oid += "." + nd.getId();
            }
            m_display.setText( oid );
        }
    }
}

class OidNode {
    protected int m_id;
    protected String m_name;

    public OidNode( int id, String name ) {
        m_id = id;
        m_name = name;
    }

    public int getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }

    public String toString() {
        return m_name;
    }
}
