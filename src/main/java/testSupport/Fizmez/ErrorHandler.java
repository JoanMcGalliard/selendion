package support.Fizmez;
// David's Error Handler
// 20010610 v1.0 Basic Functionality
// 20040316 v1.1 Changed setErrorLevel to setLoggingLevel
// 20051121 v1.2 Changed indenting to tabs
import java.util.*;

class ErrorHandler
{
	public final static int ERROR=3;
	public final static int WARNING=2;
	public final static int NOTICE=1;
	public final static int DEBUG=0;
	private int loggingLevel=WARNING;
	
	ErrorHandler(int level)
	{
		loggingLevel=level;
	}

	ErrorHandler()
	{
		loggingLevel=WARNING;
	}
	
	public void setLoggingLevel(int level)
	{
		loggingLevel=level;
	}

	public int getLoggingLevel()
	{
		return(loggingLevel);
	}

	public boolean reporting(int testLevel)
	{
		if (loggingLevel<=testLevel)
		{
			return(true);
		}
		else
		{
			return(false);
		}
	}

	private void write(String s)
	{
		System.out.println(new Date(System.currentTimeMillis()).toString()+" "+s);
	}

	public void error(String s)
	{
		if (loggingLevel<=ERROR)
		{
			write("ERROR	 : "+s);
		}
		System.exit(0);
	}

	public void warning(String s)
	{
		if (loggingLevel<=WARNING)
		{
			write("WARNING : "+s);
		}
	}

	public void notice(String s)
	{
		if (loggingLevel<=NOTICE)
		{
			write("NOTICE	: "+s);
		}
	}

	public void debug(String s)
	{
		if (loggingLevel<=DEBUG)
		{
			write("DEBUG	 : "+s);
		}
	}
}
