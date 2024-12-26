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
import javax.swing.*;
//import java.awt.event.*;

public class TestFrame extends JFrame {

    public TestFrame() {
        super( "JLayeredPane Demo" );
        setSize( 256, 256 );

        JPanel content = new JPanel();
        content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS ) );
        content.setOpaque( false );

        JLabel label1 = new JLabel( "Username:" );
        label1.setForeground( Color.white );
        content.add( label1 );

        JTextField field = new JTextField( 15 );
        content.add( field );

        JLabel label2 = new JLabel( "Password:" );
        label2.setForeground( Color.white );
        content.add( label2 );

        JPasswordField fieldPass = new JPasswordField( 15 );
        content.add( fieldPass );

        getContentPane().setLayout( new FlowLayout() );
        getContentPane().add( content );
        ( ( JPanel )getContentPane() ).setOpaque( false );

        ImageIcon earth = IconLoader.loadIcon( "earth.jpg" );
        JLabel backlabel = new JLabel( earth );
        getLayeredPane().add( backlabel, Integer.valueOf( Integer.MIN_VALUE ) );
        backlabel.setBounds( 0, 0, earth.getIconWidth(), earth.getIconHeight() );
    }

    public static void main( String[] args ) {
        TestFrame frame = new TestFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
