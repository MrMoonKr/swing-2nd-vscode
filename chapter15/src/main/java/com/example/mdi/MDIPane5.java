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

package com.example.mdi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MDIPane5 extends JLayeredPane implements ComponentListener {
    public MDIPane5() {
        addComponentListener( this );
        setOpaque( true );

        // default background color
        setBackground( new Color( 244, 232, 152 ) );
    }

    public void componentHidden( ComponentEvent e ) {
    }

    public void componentMoved( ComponentEvent e ) {
    }

    public void componentShown( ComponentEvent e ) {
    }

    public void componentResized( ComponentEvent e ) {
        lineup();
    }

    public void lineup() {
        int frameHeight, frameWidth, currentX, currentY, lheight, lwidth;
        lwidth = getWidth();
        lheight = getHeight();
        currentX = 0;
        currentY = lheight;
        Component[] components = getComponents();
        for ( int i = components.length - 1; i > -1; i-- ) {
            if ( components[i] instanceof InnerFrame5 ) {
                InnerFrame5 tempFrame = ( InnerFrame5 )components[i];
                frameHeight = tempFrame.getHeight();
                frameWidth = tempFrame.getWidth();
                if ( tempFrame.isMaximized() ) {
                    tempFrame.setBounds( 0, 0, getWidth(), getHeight() );
                    tempFrame.validate();
                    tempFrame.repaint();
                } else if ( tempFrame.isIconified() ) {
                    if ( currentX + frameWidth > lwidth ) {
                        currentX = 0;
                        currentY -= frameHeight;
                    }
                    tempFrame.setLocation( currentX, currentY - frameHeight );
                    currentX += frameWidth;
                }
            }
        }
    }
}
