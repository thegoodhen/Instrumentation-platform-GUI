/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.Compiler;

/**
 * Extension of Compiler, which supports printing out the errors in the
 * statusWindow
 *
 * @author thegoodhen
 */
public class GUICompiler extends Compiler {

    private GUIPanel gp;

    public GUICompiler(GUIPanel gp) {
	this.gp = gp;
    }

    @Override
    public void issueWaring(String s) {
	gp.showText(s);//TODO: maybe pwetty colors tho?
    }

    @Override
    public void issueError(String s) {
	gp.showText(s);//TODO: maybe pwetty colors tho?
    }

}
