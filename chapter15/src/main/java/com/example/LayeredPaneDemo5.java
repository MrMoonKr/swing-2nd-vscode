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
import javax.swing.*;

import com.example.mdi.*;

public class LayeredPaneDemo5 extends JFrame {
    public LayeredPaneDemo5() {
        super( "Custom MDI: Part V" );
        setSize( 570, 400 );
        getContentPane().setBackground( new Color( 244, 232, 152 ) );

        setLayeredPane( new MDIPane5() );

        ImageIcon ii = IconLoader.loadIcon( "earth.jpg" );
        InnerFrame5[] frames = new InnerFrame5[5];
        for ( int i = 0; i < 5; i++ ) {
            frames[i] = new InnerFrame5( "InnerFrame " + i );
            frames[i].setBounds( 50 + i * 20, 50 + i * 20, 200, 200 );
            frames[i].getContentPane().add( new JScrollPane( new JLabel( ii ) ) );
            getLayeredPane().add( frames[i] );
        }

        WindowListener l = new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        };

        Dimension dim = getToolkit().getScreenSize();
        setLocation( dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2 );

        ImageIcon image = IconLoader.loadIcon( "spiral.gif" );
        setIconImage( image.getImage() );
        addWindowListener( l );
        setVisible( true );
    }

    public static void main( String[] args ) {
        LayeredPaneDemo5 mainFrame = new LayeredPaneDemo5();
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setVisible( true );
    }
}
