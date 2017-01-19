/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

/**
 *
 * @author jeevan
 */
public enum OthelloPiece
{
    BLACK, WHITE, EMPTY;
    public static OthelloPiece identify(String s)
    {
        switch(s)
        {
            case "BLACK" : return BLACK;
            case "WHITE" : return WHITE;
            default : return EMPTY;
        }
    }
}