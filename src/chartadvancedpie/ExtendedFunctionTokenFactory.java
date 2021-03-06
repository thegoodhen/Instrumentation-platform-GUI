/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import shuntingyard.AllocBytesOnStackUserFunctionTokenFactory;
import shuntingyard.AllocIntsOnStackUserFunctionTokenFactory;
import shuntingyard.CompilerException;
import shuntingyard.DelegatingFactory;
import shuntingyard.FunctionToken;
import shuntingyard.FunctionTokenFactory;
import shuntingyard.SetStackByteUserFunctionTokenFactory;
import shuntingyard.SetStackIntUserFunctionTokenFactory;
import shuntingyard.StackByteUserFunctionTokenFactory;
import shuntingyard.StackIntUserFunctionTokenFactory;
import shuntingyard.Token;
/**
 * DelegatingFactory, used to create all built-in function tokens, specific for
 * the GUI.
 * @author thegoodhen
 */
public class ExtendedFunctionTokenFactory extends DelegatingFactory{
	public ExtendedFunctionTokenFactory()
	{
		this.addSubFactory(new PrintNumberUserFunctionTokenFactory());
		this.addSubFactory(new PrintTextUserFunctionTokenFactory());
		this.addSubFactory(new FindGUIElementByNameUserFunctionTokenFactory());
		this.addSubFactory(new getGEValUserFunctionTokenFactory());
		this.addSubFactory(new SetGEValUserFunctionTokenFactory());
		this.addSubFactory(new GetPropertyIDByNameUserFunctionTokenFactory());
		this.addSubFactory(new GetIntPropertyUserFunctionTokenFactory());
		this.addSubFactory(new SetIntPropertyUserFunctionTokenFactory());
		this.addSubFactory(new GetFloatPropertyUserFunctionTokenFactory());
		this.addSubFactory(new SetFloatPropertyUserFunctionTokenFactory());
		this.addSubFactory(new SetLinePropertyUserFunctionTokenFactory());
		this.addSubFactory(new GetLinePropertyUserFunctionTokenFactory());
		this.addSubFactory(new AllocBytesOnStackUserFunctionTokenFactory());
		this.addSubFactory(new StackByteUserFunctionTokenFactory());
		this.addSubFactory(new SetStackByteUserFunctionTokenFactory());
		this.addSubFactory(new AllocIntsOnStackUserFunctionTokenFactory());
		this.addSubFactory(new StackIntUserFunctionTokenFactory());
		this.addSubFactory(new SetStackIntUserFunctionTokenFactory());
		this.addSubFactory(new HasTagUserFunctionTokenFactory());
		this.addSubFactory(new newSliderUserFunctionTokenFactory());
		this.addSubFactory(new newDisplayUserFunctionTokenFactory());
		this.addSubFactory(new newTabUserFunctionTokenFactory());
		this.addSubFactory(new EvalAtUserFunctionTokenFactory());
		this.addSubFactory(new RGBAUserFunctionTokenFactory());
		this.addSubFactory(new CastToIntUserFunctionTokenFactory());
		this.addSubFactory(new CastToByteUserFunctionTokenFactory());
		this.addSubFactory(new SendGlobalKeysUserFunctionTokenFactory());
		this.addSubFactory(new SendKeysUserFunctionTokenFactory());
		this.addSubFactory(new MapUserFunctionTokenFactory());
		this.addSubFactory(new TypeMapUserFunctionTokenFactory());
		this.addSubFactory(new InstanceMapUserFunctionTokenFactory());
		this.addSubFactory(new SetRegisterUserFunctionTokenFactory());
		this.addSubFactory(new AddToRegisterUserFunctionTokenFactory());
		this.addSubFactory(new CopyRegisterUserFunctionTokenFactory());
		this.addSubFactory(new RegistersUserFunctionTokenFactory());
		this.addSubFactory(new ConnectToUserFunctionTokenFactory());
		//super();
		//TODO: write the custom functions here
	}


	@Override
	public String getRegex() {
		return "([A-Za-z][A-Za-z0-9]*)\\(";
	}

	@Override
	public Token generateInstance(String tokenString) {
		return new FunctionToken(tokenString);
	}

	/**
	 * Override to make sure the parenthesis, needed to correctly identify the Token,
	 * doesn't become part of the token string itself.
	 * @param m
	 * @return 
	 */
	@Override
	public int calculateRegexMatchEnd(Matcher m) {
		if (m.groupCount() > 0) {
			this.setRegexEnd(m.end(1));
		}
		return this.getRegexEnd();
	}

	@Override
		public int getNiceness()
		{
			return -1;
		}

		

	@Override
	public Token create(String tokenString, int position) throws CompilerException
	{
		Pattern p=Pattern.compile("([A-Za-z][A-Za-z_0-9]*)\\(",0);
		//Pattern p=Pattern.compile("(.*?)\\s*\\r?\\n");
		Matcher m=p.matcher(tokenString);
		
		if(m.find(position) && m.start()==position)
		{
			//String slepice=m.group(1).trim();
			Token t=super.create(m.group(1).trim(), 0);
			this.setRegexEnd(m.end(1));
			return t;
		}
		else
		{
			//System.err.println("Did not even find a generic function token!");
		}
		return null;
	}
	
}
