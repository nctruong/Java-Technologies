package com.quipoz.framework.util;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.qre.utils.dataaccess.support.SimpleDbDialect;
import com.csc.qre.utils.dataaccess.support.SimpleDbDialectManager;
import com.ia.framework.dialect.FrameworkDialectFactory;
import com.quipoz.COBOLFramework.common.exception.ExtMsgException;
import com.quipoz.COBOLFramework.database.QPBaseDataSource;
import com.quipoz.framework.datatype.BaseData;
import com.quipoz.framework.datatype.BaseScreenData;
import com.quipoz.framework.datatype.DateData;
import com.quipoz.framework.datatype.DecimalData;
import com.quipoz.framework.datatype.FastNIBase;
import com.quipoz.framework.datatype.FixedLengthStringData;
import com.quipoz.framework.datatype.IntegerData;
import com.quipoz.framework.datatype.RPGDateData;
import com.quipoz.framework.datatype.RPGTimeData;
import com.quipoz.framework.datatype.RPGTimestampData;
import com.quipoz.framework.datatype.StringBase;
import com.quipoz.framework.datatype.TimeData;
import com.quipoz.framework.datatype.TimestampData;
import com.quipoz.framework.error.BaseMessage;
import com.quipoz.framework.error.MessageList;
import com.quipoz.framework.exception.WebServerException;
import com.quipoz.framework.screenmodel.ScreenModel;
import com.quipoz.framework.util.log.QPLogger;
import com.quipoz.framework.webcontrol.ControllerServlet;

/**
 * AppVars holds global variables. Typically constructed as just about the first thing an application does, it remains
 * until the session ends. The Global Variables include
 * <ul>
 * <li>Messages, diagnostics, timings
 * <li>Userid, user related things
 * <li>Instances of formatters
 * <li>Any application globals
 * <li>Etc
 * </ul>
 * <p>
 * All SQL should execute through AppVars for the purposes of tracking and diagnositcs.
 * <p>
 * AppVars is rarely used as a Class in itself. There are layers of AppVars to implement Global functionality. A typical
 * scenario might be: Application AnApp was originally built on a framework called ToppFw. It was all built in COBOL,
 * and so we have
 * <ul>
 * <li>AppVars - this Class.
 * <li>CobolAppVars - Extends AppVars, contains global variables specific to COBOL, probably information about arrays.
 * <li>ToppFwAppVars - Extends CobolAppVars, contains global variables pertaining to the operation of the emulated
 * ToppFw framework.
 * <li>AnAppAppVars - Extends ToppFwAppVars, contains application globals. The database specific connect, free
 * connection and other database specific SQL code will probably reside here too.
 * </ul>
 * <p>
 * AppVars stores an instance of itself in a ThreadLocal, so the current object doesn't need to be passed around but can
 * be obtained by
 * <p>
 * AnAppAppVars = (AnAppAppVars)AppVaras.getInstance();
 * <p>
 * In a J2EE environment, AppVars is passed back and forth between the Web and Application layers. Rather than replacing
 * the current object in each layer at every invokation, the contents are copied over - constantly changing the object
 * id caused too many problems. This is done through the set(AppVars) method. It is important that all data that needs
 * to be copied is copied. To enforce this, there should be a static block of code in each subclass to test-copy, and
 * detect if any variables are not copied. TODO Enter Description Here
 *
 * @author Quipoz - Chris Hulley
 * @version 5.11 September 2007
 */
@SuppressWarnings("unchecked")
public class AppVars implements Cloneable, Serializable {
	/**
	 * The logger in effect. Delegates to log4j, but may be overridden in a language or application AppVars if some
	 * other form of logging (e.g. Tivoli) is required.
	 */
	protected static QPLogger log = QPLogger.getQPLogger(AppVars.class);

	private static final Logger LOGGER = LoggerFactory.getLogger(AppVars.class);

    /**
	 * In the NonConverstional model (StatelessSessionFacade), used to test system action after a Transfer or Switch
	 * screen has occurred. Not used in the Conversational Model (StatefulSessionFacade).
	 */
	public static final String ACTION = "Transfer";

    /**
	 * Reference for storing things
	 */
	protected static final String APPVARS = "AppVars";

	private static final String BCLASSB = "(Class)";

	private static final String BMETHODB = "(Method)";

	private static final String BP = " +";

	private static final String CINDEX = ", index ";

	private static final String COLLATE_ERR = "Error trying to get the collating sequence";

	private static final String COMMA_SEP = "#,##0.000";

	private static final String COMMIT = "Commit";

	private static final String COMP = "Comp";

	private boolean isMenuDisplayed = true;

	/**
	 * Name of the database manager.
	 */
	public static String DBM = "DB2";

	private static final String DELIMITER = "|";

	private static final Comparator DESCENDING = Collections.reverseOrder();

	/**
	 * List of Class names NOT to report in dignostic messages.
	 * <p>
	 * When diagnostics are formatted, a Class stack might be MyProg <br>
	 * called SQLVars <br>
	 * called AppVars.executeQuery <br>
	 * had an error, and called addDiagnostic
	 * <p>
	 * The place we want the error recorded from is MyProg and the line number.
	 */
	private static String dontReportStatic = "AppVars/ApplicationVariables/FileCode/DAM/SQLVars/Logger";

	private static final String DUMMYHASH = "DummyHash";

	private static final String EHACCESS = "</access>";

	private static final String EHCODE = "</code>";

	private static final String EHMSG = "</msg>";

	private static final String EHTIME = "</time>";

	private static final String EHWHERE = "</where>";

	private static final String ERR_NULL1 = "Error - null1";

	private static final String ERR_NULL2 = "Error - null2";

	private static final String FIELD_APO = "Field '";

	/**
	 * Local basic date format
	 */
	public static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

	private static final String HACCESS = "<access>";

	private static final String HCODE = "<code>";

	/**
	 * The class used to format HTML. Note that its value is supplied in a static block. The class itself was originally
	 * designed as static, but the need for language and application specific formatting meant a change was needed. Note
	 * that superclasses of AppVars should probably reassign this variable in their own static blocks.
	 */
	public static HTMLFormatter hf = null;

	private static final String HMSG = "<msg>";

	private static final String HTIME = "<time>";

	private static final String HTML_39 = "&#039;";

	private static final String HWHERE = "<where>";

	private static final int I314159 = 314159;

	private static final long L3141592653 = 3141592653L;

	private static final String LOCK = "LOCK";


	private static int messageLimit = 50;

	private static final int MINUS_302 = -302;

	private static final int MINUS_99999 = -99999;

	private static final String NL_OR = "\nOR";

	private static final String NL_T = "\n\t";

	/**
	 * In the NonConverstional model (StatelessSessionFacade), used to test system action after a Transfer or Switch
	 * screen has occurred. Not used in the Conversational Model (StatefulSessionFacade).
	 */
	public static final String NO = "$Branch/";

	/**
	 * In the NonConverstional model (StatelessSessionFacade), used to test system action after a Transfer or Switch
	 * screen has occurred. Not used in the Conversational Model (StatefulSessionFacade).
	 */
	public static final String NOACTION = "$Branch/Transfer";

	private static final String NOT_CATERED = " not catered for yet.";

	/**
	 * Used in diagnostics to count performance related things.
	 */
	public static int perfc = 0;

	/**
	 * A hash table used to log information that may be needed for debugging, eg memory references.
	 */
	public static Hashtable performance = new Hashtable();

	/**
	 * Performance option to count the number of times a variable gets updated. Turn OFF for production!
	 */
	public static boolean performanceCountUpdates = false;

	/**
	 * Variable to say how many times a variable needs to be changed before we can consider it to be worth reporting. No
	 * effect unless {@link #performanceCountUpdates} is true.
	 */
	public static int performanceDumpCountsOver = 256;

	private static Random random = new Random(System.currentTimeMillis());

	private static final String RED = "<font color=red>";

	private static final String RET = "\r";

	private static final long serialVersionUID = 511L;

	// fix bug940
	public static StaticMessages sm = new StaticMessages();

	/**
	 * This is a testing variable that allows you to run things that require XML to be loaded outside of a Server. It
	 * should never be used for deployed online applications.
	 */
	public static boolean standaloneEnvironment = false;

	private static final String TAB = "\t";

	/**
	 * Include Identity columns on table create. This syntax is valid for DB2. For Oracle, a more labour-intensive
	 * method must be used.
	 */
	public static String TEMP_INCLUDE_IDENTITY = " INCLUDING IDENTITY ";

	/**
	 * Are temporary tables logged. This syntax is valid for DB2.
	 */
	public static String TEMP_NOT_LOGGED = " NOT LOGGED ";

	/**
	 * The schema that will be used to create global temporary tables. For example, DB2 "SESSION", Oracle anyvalue you
	 * like. Do not change the value here; change it in the subclass of AppVars that implements the database specifics,
	 * in the Static block that should exist in that Class.
	 */
	public static String TEMP_SCHEMA = "SESSION";

	/**
	 * The schema that will be used to create global temporary tables. For example, DB2 "SESSION.", Oracle anyvalue you
	 * like. Do not change the value here; change it in the subclass of AppVars that implements the database specifics,
	 * in the Static block that should exist in that Class.
	 */
	public static String TEMP_SCHEMA_DOT = TEMP_SCHEMA + '.';

	/**
	 * The signature by which SQL statements that create global temporary tables can be recognised. Externalised as it
	 * changes across database implementations. For example, DB2 "DECLARE GLOBAL", Oracle "CREATE GLOBAL". Do not change
	 * the value here; change it in the subclass of AppVars that implements the database specifics, in the Static block
	 * that should exist in that Class.
	 */
	public static String TEMP_TABLE_CREATE_SIG = "DECLARE GLOBAL";

	/**
	 *  * The signature by which SQL statements that create global temporary tables can be
	 * recognised. Externalised as it changes across database implementations.
	 * For example, SQL "SELECT * INTO",
	 * Oracle "CREATE GLOBAL". Do not change the
	 * value here; change it in the subclass of AppVars that implements the database specifics, in
	 * the Static block that should exist in that Class.
	 */
	public static String TEMP_TABLE_CREATE_SIG_SQL = "SELECT * INTO";

	/**
	 * The signature by which SQL statements that drop global temporary tables can be recognised. Externalised as it
	 * changes across database implementations. Should be the same in all DBs, but you need to redo it if temp_schema
	 * changes. Do not change the value here; change it in the subclass of AppVars that implements the database
	 * specifics, in the Static block that should exist in that Class.
	 */
	public static String TEMP_TABLE_DROP_SIG = "DROP TABLE " + TEMP_SCHEMA + ".";

	/**
	 * Can indexes be created on temporary tables. For example, DB2 no, Oracle yes. Do not change the value here; change
	 * it in the subclass of AppVars that implements the database specifics.
	 */
	public static boolean TEMP_TABLE_INDEXES_ALLOWED = false;

	private static final String TYPE = "Type ";

	public static final String UNKNOWN = "(Unknown)";

	private boolean isTableSchemaEnabled = false;

	private Hashtable<String, String> tableSchemas = null;

	/**
	 * A String to indicate to an SQL statement to not take locks. Ie, dirty read. For example, DB2 = "WITH UR", Oracle
	 * = not supported. Do not change the value here; change it in the subclass of AppVars that implements the database
	 * specifics, in the Static block that should exist in that Class.
	 */
	public static String WITH_UR = " WITH UR";

	/**
	 * Sanity check data across EJB layers.
	 * <p>
	 * When BaseModel passes over the EJB layer from Client to Server, some of the objects therein may have been changed
	 * by user interaction.
	 * <p>
	 * In Conversational mode, the program running on the Server will have its own copies of these objects, which
	 * include AppVars (normally an extension to it), screen vars, and possibly the screen model.
	 * <p>
	 * One way of proceeding is to re-set the objects in the running program to the new copies that result from the
	 * serialisation/ deserialisation across the EJB layer. However, this is not always what is wanted, there may be
	 * local copies, redefinitions etc in place.
	 * <p>
	 * Another way is to copy the values of each individual field back with the updated values. This can be done with
	 * reflection, but incurs a processing cost.
	 * <p>
	 * A way has been implemented, and that is via a set() method, which is supposed to copy the values back on a field
	 * by field basis.
	 * <p>
	 * Primitives can be simply reassigned. So can private objects, as they are always fetched via a getter. (True, code
	 * <u>could</u> save a local copy, but this has been avoided.)
	 * <p>
	 * Other objects should have their internal data copied across.
	 * <p>
	 * This method is the first of two support methods which are intended to be called by Classes that use such
	 * synchronisation methods, to sanity check the code.
	 * <p>
	 * As this is done only once per JVM initialisation, the overhead is not great. Once the code is in production, you
	 * could comment it out to save a bit of time, though.
	 * <p>
	 * As this code requires access to private and protected fields, it needs to be duplicated into each superclass that
	 * wants to do this checking.
	 */
	static {
		 //* Assign the base formatter class. Extensions of AppVars should assign more specific versions as needed in their copy of this static block.
		hf = new HTMLFormatter();
		sm = new StaticMessages();
	}

	/**
	 * Static method which attempts to locate an instance of AppVars on this Thread and log the passed message to it as
	 * a Diagnostic.
	 * <p>
	 * If the instance is found, the message is logged together with the Userid which can be retrieved from that
	 * instance. If not, then a default userid will be used.
	 *
	 * @param msg String containing some important text to report.
	 */
	public static void addStaticDiagnostic(final String msg) {
		addStaticDiagnostic(msg, AppConfig.LOW_DEBUG);
	}

	/**
	 * Just like addDiagnostic, except called from Classes that want to log a message, but don't have a copy of the
	 * Application's AppVars. This Method locates the Current Instance of AppVars, if it exists, and logs the diagnostic
	 * there. If not, the message is logged directly.
	 *
	 * @param msg String containing some important text.
	 * @param level importance level. If greater than {@link AppConfig#debugLevel} the message will not be seen.
	 */
	public static void addStaticDiagnostic(final String msg, final int level) {
		AppVars av = getInstance();
		if (av != null && av.appConfig != null) {
			av.addDiagnostic(msg, level);
		} else {
			if (level == AppConfig.WARNING) {
				LOGGER.warn(UNKNOWN, msg);
			} else if (level == AppConfig.ERROR) {
				LOGGER.error(UNKNOWN, msg);
			} else {
				LOGGER.debug(UNKNOWN, msg);
			}
		}
	}
	/**
	 * See {@link #addStaticDiagnostic(String)}. Defaults the message level to error.
	 *
	 * @param msg String containing some important text to report.
	 */
	public static void addStaticError(final String msg) {
		addStaticDiagnostic(msg, AppConfig.ERROR);
	}

	/**
	 * See {@link #addStaticDiagnostic(String)}. Defaults the message level to error.
	 *
	 * @param msg String containing some important text to report.
	 * @param e additional error information to print a stack trace for.
	 */
	public static void addStaticError(final String msg, final Exception e) {
		addStaticError(msg);
		addStaticError(e);
	}

	/**
	 * See {@link #addStaticDiagnostic(String)}. Defaults the message level to error. Adds the stack trace to the
	 * message.
	 *
	 * @param e exception to log.
	 */
	public static void addStaticError(final Throwable e) {
		StackTraceElement[] errs = e.getStackTrace();
		String s = e.getClass().getSimpleName();
		s += " " + e.getMessage();
		for (int i = 0; i < errs.length; i++) {
			s += "\n\t\tat " + errs[i].toString();
		}
		Throwable cause = e.getCause();
		while (cause != null) {
			s += "\nCaused by " + cause.getClass().getSimpleName() + " " + cause.getMessage();
			errs = cause.getStackTrace();
			for (int i = 0; i < errs.length; i++) {
				s += "\n\t\tat " + errs[i].toString();
			}
			cause = cause.getCause();
		}
		addStaticError(s);
	}

	/**
	 * Static method which attempts to locate an instance of AppVars on this Thread and log the passed message to it as
	 * a Warning.
	 * <p>
	 * If the instance is found, the message is logged together with the Userid which can be retrieved from that
	 * instance. If not, then a default userid will be used.
	 *
	 * @param msg String containing some important text to report.
	 */
	public static void addStaticWarning(final String msg) {
		addStaticDiagnostic(msg, AppConfig.WARNING);
	}

	/**
	 * See {@link #addStaticDiagnostic(String)}.
	 *
	 * @param msg String containing some important text to report.
	 */
	public static void debug(final String msg) {
		addStaticDiagnostic(msg, 0);
	}

	/**
	 * Empty block.
	 */
	protected static void doNothing() {
	}
	
	/**
	 * Build a String list from the the cascading map
	 *
	 * @param map
	 * @return
	 *
	 * @author Wayne Yang 2010-02-09
	 */
	public List<String> getCascadingArray(Map<String, Map<String, String>> map) {
		List<String> list = new ArrayList<String>();
		if (map != null) {
			for (String s : map.keySet()) {
				Map<String, String> tempMap = map.get(s);
				for (String s1 : tempMap.keySet()) {
					list.add(s + s1 + tempMap.get(s1));
				}
			}
		}
		return list;
	}

	/**
	 * Testing/debugging method to scan through an Object, probably a program, find all objects recursively, find all
	 * BaseScreenData fields recursively, and dump out in descending order the fields that have been updated, and how
	 * often.
	 * <p>
	 * Use of this method requires code changes. The functionality must be turned on in AppVars (variable
	 * performanceCountUpdates), and this method must be called at the end of the problem program.
	 * <p>
	 * Usefulness is that identifying very often updated variables may lead to performance improvements if they can be
	 * replaced by a java primitive.
	 *
	 * @param prog - object to dump
	 * @param ht - a HashTable, can and should be null when called from the problem proigram.
	 * @param objs - a HashTable, can and should be null when called from the problem proigram.
	 * @param qcn - qualified class name, can and should be null when called from the problem proigram.
	 */
	public static void dumpTimesUpdated(final Object prog, Hashtable ht, Hashtable objs, String qcn) {

		String passedqcn = qcn;

		if (prog == null) {
			return;
		}

		Field[] flds = prog.getClass().getFields();

		if (flds == null || flds.length == 0) {
			return;
		}

		if (ht == null) {
			ht = new Hashtable(5000);
		}

		if (objs == null) {
			objs = new Hashtable(100);
		}

		if (qcn == null) {
			qcn = prog.getClass().getName() + ".";
		}

		objs.put(prog.hashCode() + "", "");

		for (int i = 0; i < flds.length; i++) {
			try {
				Object o = flds[i].get(prog);
				if (o == null) {
					continue;
				}
				if (o instanceof BaseScreenData) {
					BaseScreenData bsd = (BaseScreenData) o;
					String key = qcn + flds[i].getName();
					ht.put(key, Integer.valueOf(bsd.timesUpdated));
				} else if (o instanceof BaseScreenData[]) {
					BaseScreenData[] bsd = (BaseScreenData[]) o;
					for (int j = 1; j < bsd.length; j++) {
						String key = qcn + flds[i].getName() + "[" + j + "]";
						ht.put(key, Integer.valueOf(bsd[j].timesUpdated));
					}
				} else {
					if (objs.get(o.hashCode() + "") == null) {
						dumpTimesUpdated(o, ht, objs, qcn + flds[i].getName() + ".");
					}
				}
			} catch (Exception e) {
				// This is very rarely executed diagnostic code; ignore any problem here.
				doNothing();
			}
		}

		if (passedqcn == null) {
			if (performanceDumpCountsOver > 1) {
				Enumeration ken = ht.keys();
				while (ken.hasMoreElements()) {
					String key = (String) (ken.nextElement());
					Integer i = (Integer) ht.get(key);
					if (i.intValue() <= performanceDumpCountsOver) {
						ht.remove(key);
					}
				}
			}
			Enumeration ken = ht.keys();
			String[] things = new String[ht.size()];
			int ind = -1;
			while (ken.hasMoreElements()) {
				ind++;
				String key = (String) (ken.nextElement());
				Integer i = (Integer) ht.get(key);
				things[ind] = QPUtilities.padLeft(i + "", 9) + " " + key;
			}
			Arrays.sort(things);
			addStaticDiagnostic("Dump of variable usage");
			for (int i = things.length - 1; i >= 0; i--) {
				addStaticDiagnostic(things[i]);
			}
		}
	}

	/**
	 * Method formatDiagnosticLongSQL. See
	 * {@link #formatDiagnosticSQL(Object[], String, String, String, int, long, int)}. This version formats an SQL
	 * statement that took a long time.
	 *
	 * @param dparms internal log of parameters that were used in the construction of the SQL call.
	 * @param aSql String, the SQL text
	 * @param access diagnostic String identifying the SQL call type.
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. diagnostic
	 *            String saying from what code this SQL statement was called.
	 * @param sqlCode diagnostic String identifying the SQL result.
	 * @param startTs long, when the call started.
	 * @return String formatted SQL diagnostic message which will be post processed by HTMLFormatter or something like
	 *         it.
	 */
	public static String formatDiagnosticLongSQL(final Object[] dparms, String aSql, final String access, final String fromWhere,
	        final int sqlCode, final long startTs) {
		int p = 0;
		for (int i = 1; i < 1000; i++) {
			p = aSql.indexOf('?');
			if (p < 0) {
				break;
			}
			if (dparms[i] == null) {
				break;
			}
			String s = null;
			if (dparms[i] instanceof StringBase) {
				s = Str.APOST + ((BaseData) dparms[i]).toString() + Str.APOST;
			} else if (dparms[i] instanceof BaseData) {
				s = ((BaseData) dparms[i]).toString();
			} else if (dparms[i] instanceof Integer || dparms[i] instanceof Long) {
				s = dparms[i].toString().trim();
			} else {
				s = Str.APOST + dparms[i] + Str.APOST;
			}
			aSql = aSql.substring(0, p) + s + aSql.substring(p + 1);
		}

		return "<SQLLongmsg>" + HMSG + aSql + EHMSG + HACCESS + access + EHACCESS + HWHERE + fromWhere + EHWHERE
		        + HCODE + QPUtilities.formatI(sqlCode, Num.I4) + EHCODE + HTIME + QPUtilities.sSecs(startTs) + EHTIME
		        + "</SQLLongmsg>";
	}

	/**
	 * Method formatDiagnosticSQL. Not intended for application use.
	 *
	 * @param dparms internal log of parameters that were used in the construction of the SQL call.
	 * @param aSql String, the SQL text
	 * @param access diagnostic String identifying the SQL call type.
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. diagnostic
	 *            String saying from what code this SQL statement was called.
	 * @param sqlCode diagnostic String identifying the SQL result.
	 * @param startTs long, when the call started.
	 * @param rows number of rows affected.
	 * @return String formatted SQL diagnostic message which will be post processed by HTMLFormatter or something like
	 *         it.
	 */
	public static String formatDiagnosticSQL(final Object[] dparms, String aSql, final String access, final String fromWhere,
	        final int sqlCode, final long startTs, final int rows) {
		int p = 0;
		if (aSql != null) {
			for (int i = 1; i < Num.I1K; i++) {
				p = aSql.indexOf('?');
				if (p < 0) {
					break;
				}
				if (dparms[i] == null) {
					break;
				}
				String s = null;
				if (dparms[i] instanceof StringBase) {
					s = Str.APOST + ((BaseData) dparms[i]).toString() + Str.APOST;
				} else if (dparms[i] instanceof BaseData) {
					s = ((BaseData) dparms[i]).toString();
				} else if (dparms[i] instanceof Integer || dparms[i] instanceof Long) {
					s = dparms[i].toString().trim();
				} else if (dparms[i] instanceof Integer) {
					s = dparms[i].toString().trim();
				} else {
					s = Str.APOST + dparms[i] + Str.APOST;
				}
				aSql = aSql.substring(0, p) + s + aSql.substring(p + 1);
			}
		}

		return "<SQLmsg>" + HMSG + aSql + EHMSG + HACCESS + access + EHACCESS + HWHERE + fromWhere + EHWHERE + HCODE
		        + QPUtilities.formatI(sqlCode, Num.I4) + EHCODE + HTIME + QPUtilities.sSecs(startTs) + EHTIME
		        + ((rows >= 0) ? "<rows>" + rows + "</rows>" : "") + "</SQLmsg>";
	}

	/**
	 * Getter for {@link #dontReportStatic}.
	 *
	 * @return {@link #dontReportStatic}.
	 */
	public static String getDontReport() {
		if (dontReportStatic == null) {
			if (AppVars.getInstance() != null) {
				dontReportStatic = AppVars.getInstance().dontReport;
			}
		}
		return dontReportStatic;
	}

	/**
	 * Get thread AppVars instance, if available. The intent is every thread will save the current instance of AppVars
	 * or its subclass so we don't have to pass this instance around.
	 *
	 * @return as per description.
	 */
	public static <T extends AppVars> AppVars getInstance() {
		return (AppVars) ThreadLocalStore.get(APPVARS);
	}

	/**
	 * Getter for {@link #standaloneEnvironment}.
	 *
	 * @return {@link #standaloneEnvironment}.
	 */
	public static boolean isStandaloneEnvironment() {
		return standaloneEnvironment;
	}

	/**
	 * Saves a reference to the passed Object in {@link #performance}. For use in memory leakage diagnostics.
	 *
	 * @param thing the passed Object
	 */
	public static void putAlloc(final Object thing) {
		Class[] stack = new QPUtilities.CurrentClassGetterArray().getClassContexts();
		String s = "";
		for (int i = 2; i < stack.length; i++) {
			s += stack[i].getSimpleName() + Str.SYS_NL;
		}
		Object[] o = new Object[] { new WeakReference(thing), s };
		performance.put(new Timestamp(System.currentTimeMillis()).toString() + ' ' + AppVars.performance.size() + ' '
		        + thing.getClass().getSimpleName(), o);
	}

	/**
	 * Stores an instance of AppVars in the ThreadLocal. See also {@link #getInstance()}.
	 *
	 * @param pAV instance to store.
	 */
	public static <T extends AppVars> void setInstance(final T pAV) {
		ThreadLocalStore.put(APPVARS, pAV);
	}

	/**
	 * Setter for {@link #standaloneEnvironment}.
	 *
	 * @param pStandaloneEnvironment new value for {@link #standaloneEnvironment}.
	 */
	public static void setStandaloneEnvironment(final boolean pStandaloneEnvironment) {
		AppVars.standaloneEnvironment = pStandaloneEnvironment;
	}

	/**
	 * A String representing the Active Field. It may be on a screen. It was moved here from ScreenModel/DataModel
	 * because values are lost in the StatefulSessionBean model.
	 */
	private String activeField;

	/**
	 * A differentiation between messages displayed in a list, and messages displayed as popups. Often unused.
	 */
	private StringBuffer alerts;

	//Fixed bug 712 Tom Chi
	public HashMap<String,ArrayList<FixedLengthStringData>> alocnoMap = new HashMap<String,ArrayList<FixedLengthStringData>>();

	/**
	 * Ther current application configuration, as read from the XML.
	 */
	private AppConfig appConfig;

	/** Application Use field 1, loaded from the Application Configuration XML. */
	public String ApplicationUse0 = null;

	/** Application Use field 2, loaded from the Application Configuration XML. */
	public String ApplicationUse1 = null;

	/** Application Use field 11, loaded from the Application Configuration XML. */
	public String ApplicationUse10 = null;

	/** Application Use field 12, loaded from the Application Configuration XML. */
	public String ApplicationUse11 = null;

	/** Application Use field 13, loaded from the Application Configuration XML. */
	public String ApplicationUse12 = null;

	/** Application Use field 3, loaded from the Application Configuration XML. */
	public String ApplicationUse2 = null;

	/** Application Use field 4, loaded from the Application Configuration XML. */
	public String ApplicationUse3 = null;

	/** Application Use field 5, loaded from the Application Configuration XML. */
	public String ApplicationUse4 = null;

	/** Application Use field 6, loaded from the Application Configuration XML. */
	public String ApplicationUse5 = null;

	/** Application Use field 7, loaded from the Application Configuration XML. */
	public String ApplicationUse6 = null;

	/** Application Use field 8, loaded from the Application Configuration XML. */
	public String ApplicationUse7 = null;

	/** Application Use field 9, loaded from the Application Configuration XML. */
	public String ApplicationUse8 = null;

	/** Application Use field 10, loaded from the Application Configuration XML. */
	public String ApplicationUse9 = null;

	/**
	 * The name of the current application. The ControllerServlet for the application under consideration should set
	 * this.
	 */
	private String appName = "";

	// hold the list of autonum unique number.
	public List<Long> autonumList = new ArrayList<Long>();

	private transient String businessdate="";

	// add the businessDateFlag and businessdate variable for displaying the business date on home page or not.(xma3 2008-08-21)
	private boolean businessDateFlag = false;
	//IPAE-188 Takaful processing for P&C business start
	private boolean isTakaful = false;
	
	
	public boolean isTakaful() {
		return isTakaful;
	}

	public void setTakaful(boolean isTakaful) {
		this.isTakaful = isTakaful;
	}
	//IPAE-188 Takaful processing for P&C business Ends
	/**
	 * In the NonConversational example provided, if this is on, a Database will be searched for recovery information.
	 * This is basically AppVars serialised to a database.
	 */
	protected int checkedForResume = 0;

	/**
	 * Used for ordering lists.
	 */
	private String collateChar = "Z";

	//end
	//diff policy with life
	private String[][] companyBranchArray = null;

	/**
	 * A String representing the Field that the cursor was in, just after a screen was displayed. It is differentiated
	 * from the Active Field, which is also used to set which field the cursor should be placed in when the screen is
	 * next displayed, and which is cleared just after screen display. This field retains the value the cursor was in,
	 * for later use.
	 */
	private String cursorField;

	/**
	 * Checks if a connection was established to the database. Used only at the very start of processing.
	 */
	public String dbConnectOK = null;

	/**
	 * Loaded from the Application Configuration XML. Up to you how you use it. Intended for System-like use.
	 */
	protected int debugAllowed = 0;

	/**
	 * Area where diagnostics are written. In most implementations, a utility screen should be available where these
	 * messages can be examined; they are only used for debugging and analysis.
	 */
	private MessageList diagnostics = new MessageList();

	/**
	 * Initial value of Classes not to report in static diagnostics. Example: if we are tracing SQL calls, we know the
	 * call will eventually be done by AppVars. The call to the method in AppVars may be from a Data Access Module
	 * (DAM). We don't really want to know that the SQL call was done in AppVars or the DAM, we want to know which line
	 * of application code initiated it.
	 */
	public String dontReport = dontReportStatic;

	/**
	 * This array is used to hold parameters put into SQL, for debugging purposes. In Java 1.4, you can get the
	 * parameters out of the prepared statement, and this will be unnecessary.
	 */
	private Object[] dparms = new Object[Num.I1K];

	/**
	 * Intended to indicate that some kind of error was detected in one or more input fields.
	 */
	public boolean fieldErrorsExist = false;

	/**
	 * Contains the field positions of variables on the screen. This should be cleared before each display, in
	 * CodeModel. Then, the HTMLFormatter will, when asked to put a field on the screen at a known position, re-derive
	 * that position and store it in here. This array will then be scanned after screen display, for the field in which
	 * the cursor is currently positoned, and something will be set to contain the x,y position that the cursor would
	 * have been detected at, if we were still using a green screen.
	 */
	public Hashtable fieldPositions = new Hashtable();

	/**
	 * Has this instance been cleaned up.
	 */
	public boolean finalised = false;

	/**
	 * A Log of finalizable objects that should be finalized to free resources when the transaction ends. It is up to
	 * the application to log such objects in here. It is good practice to remove them if finalization gets done
	 * elsewhere. When not in online mode, it is the responsibility of the controller to do something with this list. If
	 * the JVM just ends, eg batch, no real need to do anything.
	 */
	public transient Hashtable finalizableObjects = new Hashtable();

	/**
	 * This is used to say how long has it been since a screen told the framework that it is still visible in the
	 * browser. This allows us to clean up when the user closes the browser.
	 */
	protected long heartBeat = System.currentTimeMillis();

	/**
	 * Sort of intended to indicate that some kind of internal error may have occurred. Not known to be curently used.
	 */
	public boolean internalError = false;

	public String key;

	private String[] languageArray = null;
	//end

	/**
	 * Added to store the language values.
	 */
	private String languageCode = "E";

	private Locale locale ;

	/**
	 * Internal use, diagnostics formatting.
	 */
	private String lastPreamble = "";

	/**
	 * In the NonConversational example provided, name of the Class to call to resume in.
	 */
	protected String lastRoutine = "";

	/**
	 * Text of the last SQL statement executed through this Class.
	 */
	protected String lastSQL = "";

	/**
	 * Type of the last SQL statement executed through this Class.
	 */
	public String lastSQLOp = "";

	/**
	 * Generally available field to indicate that the user is logged on.
	 */
	protected boolean loggedOn = false;

	/**
	 * Userid when logged on.
	 */
	private String loggedOnUser = "";

	/**
	 * User language, up to you to set it.
	 */
	protected FixedLengthStringData loggedOnUserLanguage = new FixedLengthStringData("ENG");

	/**
	 * Used when application level security, eg log on via user/pwd held in database is used. An example is provided
	 * that does this, but only in NonConversational mode.
	 */
	private String logonNextScreen = null;

	/**
	 * Generally available field to indicate that the user has to log on in this application. It doesn't need to be
	 * used, e.g. when the application uses Websphere, RACF or LDAP.
	 */
	protected boolean logonRequired = false;
	/**
	 * Used for synchronisation of the Main and Message frames. It is set to false when a "display screen" request is
	 * made, and to true by the common jsp that is placed at the bottom of every main screen. The message frame waits
	 * for a time (eg 10 seconds) until this flag becomes true, and then displays.
	 * <p>
	 * This is necessary as the main frame can and does raise further errors as it loads. If for some reason the error
	 * frame loaded first, then those messages wouldn't be seen.
	 */
	public boolean mainFrameLoaded = false;

	private transient HashMap<String, String> masterMenu = null;
	
	private transient HashMap<String, String> masterOtherMenu = null;

	/**
	 * Variable where application messages are stored. These messages should be presented to the user for some action.
	 */
	private MessageList messages = new MessageList();


	/**
	 * The next action to be done when the current screen is finished.
	 * <p>
	 * Not used in the Conversational Model (StatefulSessionFacade).
	 */
	private String nextAction = null;

	/**
	 * In online mode, the name of the next screen to be displayed.
	 */
	private String nextScreen = "";

	/**
	 * Used to store the Next Screen's ScreenModel, if it must be created at the time that a decision is made to tell
	 * the system that the nominated screen is indeed the next one to be displayed. The ScreenModel might need to be
	 * created at that time, rather than just before the screen is displayed, if we have initialisation information we
	 * need to put on it.
	 * <p>
	 * Not used in the Conversational Model (StatefulSessionFacade).
	 */
	private ScreenModel nextScrSm = null;

	/**
	 * Indicates if the next screen is a "Transfer Control" type, e.g. GOTO. Not used in the Conversational Model
	 * (StatefulSessionFacade).
	 */
	private boolean nextScrXCTL = false;

	private String nextSingleModelJSPScreen;

	/**
	 * Class where field prompts are stored. You know, eg press PF4 in this field to get a list of all States. A popup
	 * has the followi
	 */
	private PopupArrayList popup;

	/**
	 * Type of popups used in this Application.
	 */
	private String popupType = "";

	private String prompt = "";

	/**
	 * Resource Bundle for reading the labels
	 */
	private ResourceBundle resourceBundle = null;

	/**
	 * Time the last screen started.
	 */
	protected long screenEntry = System.currentTimeMillis();

	/* Current Session Id, for distinguishing instances */
	private String sessionId = null;

	/**
	 * Timing variable used in timing calculations, eg how long was it between requesting and obtaining an EJB.
	 */
	public long SessionTS = 0;

	/**Used to handle variations in database
	 */
	private SimpleDbDialect simpleDbDialect;

	/**
	 * Used to accumulate SQL statistics.
	 */
	private Hashtable sqlSummary = null;

	/**
	 * A general purpose object. StatefulSessionFacadeBean will set this to the currently running program for standalone
	 * code, so that access can be obtained via reflection to the code's variables. Applications must be careful to use
	 * this consistently otherwise the usual problems will result.
	 */
	private Object ssfProgramObject;

	private transient String subMenu = null;

	/**
	 * Internal use, class resolution. We're moving towards having a "locator" class, i.e. one where we detect the
	 * classpath directly via compiled code, so this will be phased out.
	 */
	private String supportPath = "";

	private String contextPath = "";

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
	    return contextPath;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setContextPath(String contextPath) {
	    this.contextPath = contextPath;
	}

	private transient Object[] systemMenu = null;
	
	private transient Object[] systemOtherMenu = null;
	
	private transient String otherMenuActive = null;
	
	/**
	 * Area where special diagnostics relating to application timing are written. In most implementations, a special
	 * screen where they can be examined should be available.
	 */
	private MessageList timings = new MessageList();

	/**
	 * Simple SQL prepared statement.
	 */
	public transient PreparedStatement tps = null;

	/**
	 * An area used to transfer stuff.
	 */
	public String transferArea = "";

	private transient StringBuilder updateLog = new StringBuilder();

	/**
	 * User description.
	 */
	private String userDescription;

	// Set NSL_COMP and NSL_SORT
	private boolean isDefaultNSL = true;

	// Set End Screen for Web-Services
	public boolean isEndScreen = false;
	/**
	 * You should probably NEVER use this constructor. Application integrity pretty much depends on the other one having
	 * been used. It's used only in a few trivial standalone tests to test things that need an AppVars passed to them,
	 * but where it won't actually be used.
	 */
	public AppVars() {
	    if (AppVars.getInstance() != null) {
	        this.setContextPath(AppVars.getInstance().getContextPath());
	    }
		setInstance(this);
		appConfig = AppConfig.getInstance(this, FrameworkDialectFactory.getInstance().getAppVarsSupport().getAppVarsName());
		if (AppConfig.getMemoryRefDebug() >= 1) {
			AppVars.putAlloc(this);
		}
		//fix bug 712 Tom Chi
		this.key = System.currentTimeMillis()+""+random.nextLong();
		//end
	}


	/**
	 * The normal constructor. It does the following:
	 * <ol>
	 * <li>Saves a copy of itself in the ThreadLocal so other Classes can get a reference to this important common
	 * module without being passed it as a parameter.
	 * <li>Creates and assigns an AppConfig, which will read the XML file containing System Configuration information.
	 * <li>Gets the collating sequence, if possible.
	 * </ol>
	 *
	 * @param pAppName name of the application. This is provided by the code that calls this constructor. This usually
	 *            comes from one of two places:
	 *            <ol>
	 *            <li>Onlines. It is contained in web.xml, fetched by the {@link ControllerServlet} and used in the
	 *            Class when AppVars is constructed.
	 *            <li>Batch. The batch controller obtains the value from somewhere, e.g. a configuration file or a
	 *            parameter.
	 *            </ol>
	 *            It is used to resolve the location of the configuration file, usually QuipozCFG.xml. If the value was
	 *            "test", there should be a System Property "Quipoz.test.XMLPath" pointing to the file.
	 */
	public AppVars(final String pAppName) {
	    if (AppVars.getInstance() != null) {
	        this.setContextPath(AppVars.getInstance().getContextPath());
	    }
		setInstance(this);
		popup = new PopupArrayList();
		appName = pAppName;
		appConfig = AppConfig.getInstance(this, pAppName);

		this.key = System.currentTimeMillis()+""+random.nextLong();

		SessionTS = System.currentTimeMillis();
		/* Get the collating character */
		Connection conn = null;
		PreparedStatement aps = null;
		ResultSet rs = null;
		if (standaloneEnvironment) {
			appConfig.setServer("localswing");
		}
		if (AppConfig.getMemoryRefDebug() >= 1) {
			AppVars.putAlloc(this);
		}
		String t = appConfig.getCollateTab();
		if (t == null || t.trim().length() == 0) {
			addDiagnostic("No COLLATE table specifid in the Configuration file. Collate high defaulted to '"
			        + collateChar + "' =hex(" + new FixedLengthStringData(collateChar + "").getHex() + ")");
			return;
		}
		String aSql = "SELECT CSEQ FROM " + appConfig.getCollateTab() + " ORDER BY CSEQ DESC";
		try {
			/*
			 * Max W it seems not supposed to be involved in transaction control then replace getDBConnection with
			 * getTempDBConnection
			 */
			conn = getTempDBConnection("AppVars");
            LOGGER.debug(String.valueOf(conn.getAutoCommit()));
		} catch (Exception e) {
			dbConnectOK = "Unable to connect to the database - " + e;
		}
		try {
			aps = prepareStatement(conn, aSql);
			rs = executeQuery(aps);
			if (fetchNext(rs)) {
				collateChar = rs.getString(1);
			}
		} catch (SQLException ex1) {
		    LOGGER.error(COLLATE_ERR + ". Reason:\n", ex1);
		    addDiagnostic("Error trying to get the collating sequence");

		} catch (Exception ex) {
			addError(COLLATE_ERR);

		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				// As we're finalising, recovery is impossible. The error does not matter..
				doNothing();
			}
			freeDBConnectionIgnoreErr(conn, aps, rs);
			try {
				reallyFreeDBConnection(conn);
			} catch (Exception e1) {
				// As we're finalising, recovery is impossible. The error does not matter..
				doNothing();
			}
		}
		//diff policy with life
		this.key = System.currentTimeMillis()+""+random.nextLong();
		//end
	}

	/**
	 * Logs the passed message to it as a Diagnostic, if the passed level is <= the reporting level contained in
	 * AppConfig and loaded from the application configuration file, usually QuipozCfg.xml.
	 *
	 * @param msg - String containing soem important text.
	 * @param level - how important that message is.
	 */
	public void addAppDiagnostic(final String msg, final int level) {
		if (level <= appConfig.getAppDebugLevel()) {
			addDiagnostic(msg, false, level);
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} converting the passed FixedLengthStringData to a
	 * String, setting the second parm to false, and using the second parm supplied to this method as the third parm of
	 * the referenced method.
	 *
	 * @param msg see the referenced method.
	 * @param level see the referenced method.
	 */
	public void addDiagnostic(final FixedLengthStringData msg, final int level) {
		addDiagnostic(msg.toString(), level);
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} converting the passed Object to a String, setting the
	 * second parm to false, and using {@link AppConfig#DEFAULT_DEBUG_LEVEL} for the third.
	 *
	 * @param msg see the referenced method.
	 */
	public void addDiagnostic(final Object msg) {
		if (msg != null) {
			addDiagnostic(msg.toString());
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} converting the passed Object to a String, passing on
	 * the second, and using {@link AppConfig#DEFAULT_DEBUG_LEVEL} for the third.
	 *
	 * @param msg see the referenced method.
	 * @param interr see the referenced method.
	 */
	public void addDiagnostic(final Object msg, final boolean interr) {
		if (msg != null) {
			addDiagnostic(msg.toString(), interr, AppConfig.DEFAULT_DEBUG_LEVEL);
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} converting the passed Object to a String and passing on
	 * the second and third parms as is.
	 *
	 * @param msg see the referenced method.
	 * @param interr see the referenced method.
	 * @param level see the referenced method.
	 */
	public void addDiagnostic(final Object msg, final boolean interr, final int level) {
		if (msg != null) {
			addDiagnostic(msg.toString(), interr, level);
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} converting the passed Object to a String, setting the
	 * second parm to false, and using the second parm supplied to this method as the third parm of the referenced
	 * method.
	 *
	 * @param msg see the referenced method.
	 * @param level see the referenced method.
	 */
	public void addDiagnostic(final Object msg, final int level) {
		if (msg != null) {
			addDiagnostic(msg.toString(), false, level);
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} passing on the String, setting the second parm to
	 * false, and using {@link AppConfig#DEFAULT_DEBUG_LEVEL} for the third.
	 *
	 * @param msg see the referenced method.
	 */
	public void addDiagnostic(final String msg) {
		addDiagnostic(msg, false, AppConfig.DEFAULT_DEBUG_LEVEL);
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} passing on the first and second parms, and using
	 * {@link AppConfig#DEFAULT_DEBUG_LEVEL} for the third.
	 *
	 * @param msg see the referenced method.
	 * @param interr see the referenced method.
	 */
	public void addDiagnostic(final String msg, final boolean interr) {
		addDiagnostic(msg, interr, AppConfig.DEFAULT_DEBUG_LEVEL);
	}

	/**
	 * Adds a Text message to the diagnostics. Reformats SQL statements for optimum display on both JSP screens and to
	 * the log.
	 *
	 * @param pMsg A String containing the message to be displayed.
	 * @param interr value for {@link #internalError}. Note, that is not known to be used.
	 * @param level if this is greater than {@link AppConfig#debugLevel}, the message will not be seen.
	 */
	public void addDiagnostic(final String pMsg, final boolean interr, final int level) {
		if (appConfig != null) {
			addMessageList(pMsg, interr, level);
		}
	}

	private void addMessageList(final String pMsg, final boolean interr, final int level) {
		String msg = pMsg;
		if (appConfig != null && level <= appConfig.debugLevel) {
			if (msg == null) {
				msg = "(Null message)";
			}
			String pre = preamble();
			if(appConfig.diagnostics && this.diagnostics != null) {
				this.diagnostics.add("<line>" + pre + msg + "</line>");
			}
			if (level <= appConfig.getDebugLevel()) {
				if (msg.indexOf("<SQL") >= 0) {
					String[] sql = QPUtilities.parseXML(msg, "msg");
					sql[1] = sql[1].trim();
					int p = sql[1].toUpperCase().indexOf("FROM");
					if (p > 0) {
						sql[1] = sql[1].substring(0, p) + NL_T + sql[1].substring(p);
					}
					p = sql[1].toUpperCase().indexOf("WHERE");
					if (p > 0) {
						sql[1] = sql[1].substring(0, p) + NL_T + sql[1].substring(p);
					}
					p = sql[1].toUpperCase().indexOf(NL_OR);
					while (p > 0) {
						sql[1] = sql[1].substring(0, p) + NL_T + sql[1].substring(p + 1);
						p = sql[1].toUpperCase().indexOf(NL_OR);
					}
					p = sql[1].toUpperCase().indexOf("ORDER BY");
					if (p > 0) {
						sql[1] = sql[1].substring(0, p) + NL_T + sql[1].substring(p);
					}
					String[] where = QPUtilities.parseXML(msg, "where");
					String[] access = QPUtilities.parseXML(msg, "access");
					String[] code = QPUtilities.parseXML(msg, "code");
					String[] time = QPUtilities.parseXML(msg, "time");
					String[] rows = QPUtilities.parseXML(msg, "rows");
					String rowstr = (rows[1] != null && rows[1].trim().length() > 0) ? ", rows=" + rows[1] : "";
					msg = where[1] + Str.BK + access[1] + ", SQLCode=" + code[1] + ", time=" + time[1] + rowstr
					        + Str.SYS_NL + TAB + sql[1] + Str.SYS_NL + TAB + postamble();
				} else {
					if (msg.indexOf(RED) >= 0) {
						try {
							int p = msg.indexOf(RED);
							msg = msg.substring(0, p) + msg.substring(p + Num.I16);
							p = msg.indexOf("</font>");
							msg = msg.substring(0, p) + msg.substring(p + Num.I7);
						} catch (Exception e) {
							// Losing a diagnostic is not a problem; crashig would be.
							doNothing();
						}
					}
					msg = msg + postamble();
				}
				msg = QPUtilities.replaceSubstring(msg, HTML_39, Str.APOST);

				if (level == AppConfig.WARNING) {
					LOGGER.warn(getUser() + DELIMITER + msg);
				} else if (level == AppConfig.ERROR) {
					LOGGER.error(getUser() + DELIMITER + msg);
				} else if (level == AppConfig.INFO) {
					LOGGER.info(getUser() + DELIMITER + msg);
				} else {
					LOGGER.debug(getUser() + DELIMITER + msg);
				}
			}
			if (interr) {
				internalError = true;
			}
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} passing on the first parm, using false as the second,
	 * and passing on the second parm to this method as the third parm to the referenced method.
	 *
	 * @param msg see the referenced method.
	 * @param level see the referenced method.
	 */
	public void addDiagnostic(final String msg, final int level) {
		addDiagnostic(msg, false, level);
	}

	/**
	 * Adds Diagnostics, probably from another AppVars
	 *
	 * @param diags A MessgeList object containing a list of messages to be displayed.
	 */
	public void addDiagnostics(final MessageList diags) {
		if (diags == null) {
			return;
		}

		while (diags.size() > 0) {
			addDiagnostic((String) diags.get(0));
			diags.remove(0);
		}
	}

	/**
	 * Adds the passed Exception's stack as a diagnostic message at the AppConfig.ERROR level.
	 *
	 * @param e the passed Exception.
	 */
	public void addError(final Exception e) {
		StackTraceElement[] errs = e.getStackTrace();
		for (int i = 0; i < errs.length; i++) {
			addDiagnostic(errs[i].toString(), AppConfig.ERROR);
		}
	}

	/**
	 * Delegates to {@link #addDiagnostic(String, boolean, int)} passing on the first parm, using false as the second,
	 * and {@link AppConfig#ERROR} for the third.
	 *
	 * @param msg see the referenced method.
	 */
	public void addError(final String msg) {
		addDiagnostic(msg, AppConfig.ERROR);
	}

	public void addExtMessage(String errCode, String msg) {

    }

	/**
	 * Adds a Text message to be displayed on screen.
	 *
	 * @param msg A String containing the message to be displayed.
	 */
	public void addFieldError(final String msg) {
		if (!fieldErrorsExist) {
			fieldErrorsExist = true;
			addMessage(msg);
		}
	}

	/**
	 * Adds a message for later display or printing.
	 *
	 * @param msg A BaseMessage object containing the message to be displayed.
	 */
	public void addMessage(final BaseMessage msg) {
		addMessageNoDebug(msg);
		if (appConfig.getDebugLevel() >= AppConfig.DEFAULT_DEBUG_LEVEL) {
			LOGGER.debug(getUser(), " " + msg);
		}
	}

	/**
	 * Adds a Text message to be displayed on screen.
	 *
	 * @param pMsg A String containing the message to be displayed.
	 */
	public void addMessage(final String pMsg) {

		String msg = QPUtilities.replaceSubstring(pMsg, "\"", Str.APOST);
		msg = QPUtilities.replaceSubstring(msg, Str.SYS_NL, "");
		msg = QPUtilities.replaceSubstring(msg, "\f", "");
		msg = QPUtilities.replaceSubstring(msg, RET, "");
		addMessage(new BaseMessage(msg));
	}

	/**
	 * Adds a message for later display or printing. This version does not display a copy on the console, presumably
	 * because this has already been done.
	 *
	 * @param msg A BaseMessage object containing the message to be displayed.
	 */
	public void addMessageNoDebug(final BaseMessage msg) {
		msg.setID(format.format(Functions.getNow().getData()));

		if (messages == null) {
			messages = new MessageList();
		}
		if (this.messages.size() >= messageLimit) {
			this.messages.remove(messages.size() - 1);
		}

		this.messages.add(0, msg);
	}

	/**
	 * Adds Messages to the Screen
	 *
	 * @param pMessages A MessgeList object containing a list of messages to be displayed.
	 */
	public void addMessages(final MessageList pMessages) {
		if (pMessages == null) {
			return;
		}

		if (this.messages == null) {
			this.messages = new MessageList();
		}
		while (this.messages.size() + pMessages.size() > messageLimit) {
			this.messages.remove(this.messages.size() - 1);
		}

		while (pMessages.size() > 0) {
			addMessage((BaseMessage) pMessages.get(pMessages.size() - 1));
			pMessages.remove(pMessages.size() - 1);
		}
	}

	/**
	 * Adds a popup message
	 *
	 * @param code see {@link PopupArrayList}.
	 * @param data see {@link PopupArrayList}.
	 */
	public void addPopup(final String code, final String data) {
		// hasFieldInError = true;
		popup.addC(code, data);
	}

	/**
	 * Adds a header to the current popup. See {@link #popup}.
	 *
	 * @param header new header.
	 */
	public void addPopupHeader(final String header) {
		popup.setHeader(header);
	}

	/**
	 * Adds a title to the current popup. See {@link #popup}.
	 *
	 * @param title new title.
	 */
	public void addPopupTitle(final String title) {
		popup.setTitle(title);
	}

	/**
	 * Adds a Text message to the timings.
	 *
	 * @param msg A String containing the message to be displayed.
	 */
	public void addTiming(final String msg) {
		if (appConfig != null) {
			if (timings != null) {
				if(appConfig.timings)
					this.timings.add(QPUtilities.logStamp() + Str.BK + msg);
				addMessageList(msg, false, AppConfig.DEFAULT_DEBUG_LEVEL);
			}
		}
	}

	/**
	 * Adds Timings, probably from another AppVars
	 *
	 * @param times A MessgeList object containing a list of messages to be displayed.
	 */
	public void addTimings(final MessageList times) {
		if (times == null) {
			return;
		}

		while (times.size() > 0) {
			addTiming((String) times.get(0));
			times.remove(0);
		}
	}

	/**
	 * Adds a message to timings about how long a web service took.
	 *
	 * @param webtime time.
	 * @param msg message.
	 */
	public void addWebTiming(final String webtime, final String msg) {
		if (appConfig != null) {
			if (appConfig.timings && this.timings != null) {
				this.timings.add(webtime + Str.BK + msg);
			}
			addMessageList(msg, false, AppConfig.DEFAULT_DEBUG_LEVEL);
		}
	}

	/**
	 * Adjusts the length of the passed value for DBCS. Function is implementation dependent. Requirement came about
	 * after it was noticed that in Oracle, a DBCS character takes up 3 bytes. Therefore, the String has to be truncated
	 * to get the same length. This implementation does nothing.
	 *
	 * @param value as description.
	 * @return possibly truncated value
	 */
	public Object adjustLengthDBCS(final Object value) {
		return value;
	}

	/**
	 * Routine available to allocate any resources eg caches. Does nothing in this implementation.
	 *
	 * @param exceptFullClassNames exclude these ones.
	 */
	public void allocateResources(final String... exceptFullClassNames) {
	}

	/**
	 * Clear underlying data structures
	 *
	 * This is a supporting method to fix bug 3159 (memory leak in creating
	 * motor quotes) where BOAppVars instances are never garbage collected after
	 * runs. The ideal way is to clear all references to BOAppVars instances but
	 * that is too difficult as BOAppVars references are spread everywhere in
	 * the application, even stored in ThreadLocal of many threads. The
	 * workaround is that AppVars instances should provide a method to clear
	 * itself in order to release memory occupied by its big fields structures,
	 * especially of HashMap or HashTable data type.
	 */
	public void clearData() {

		if (sqlSummary != null) {
			sqlSummary.clear();
			sqlSummary = null;
		}

		if (masterMenu != null) {
			masterMenu.clear();
			masterMenu = null;
		}
		
		if (masterOtherMenu != null) {
			masterOtherMenu.clear();
			masterOtherMenu = null;
		}

		if (alocnoMap != null) {
			alocnoMap.clear();
			alocnoMap = null;
		}

		if (fieldPositions != null) {
			fieldPositions.clear();
			fieldPositions = null;
		}

		if (finalizableObjects != null) {
			finalizableObjects.clear();
			finalizableObjects = null;
		}

		if (null != tableSchemas) {
			tableSchemas.clear();
			tableSchemas = null;
		}
	}

	/**
	 * Method clearNextScreen. Indicates that whatever screen may have been set up to display next, that action is being
	 * cancelled because a new one is being substituted.
	 */
	public void clearNextScreen() {
		nextScreen = "";
		nextScrXCTL = false;
	}

	/**
	 * Clears the popup list. See {@link #popup}.
	 */
	public void clearPopup() {
		popup.clear();
	}

	/**
	 * Clears the log of screen timings
	 */
	public void clearTimings() {
		this.timings.clear();
	}

	/**
	 * Clones this.
	 *
	 * @return cloned AppVars.
	 * @throws CloneNotSupportedException on error.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AppVars newAv = (AppVars) super.clone();
		return newAv;
	}

	/**
	 * Implementation of COMMIT operation. To commit updates from the last execution of COMMIT or ROLLBACK; call
	 * connection.commit();
	 */
	public void commit() {
		throw new UnsupportedOperationException("Should be implemented in a subclass.");
	}

	/**
	 * Commit a connection. Assumes this is possible. No check is made to see if the connection is closed etc. Timings
	 * are, however, done.
	 *
	 * @param conn passed connection.
	 * @throws SQLException on error.
	 */
	public void commit(final Connection conn) throws SQLException {
		commit(lastRoutine, conn);
	}

	/**
	 * Commit a connection. Assumes this is possible. No check is made to see if the connection is closed etc. Timings
	 * are, however, done.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. for
	 *            diagnostics, where the commit was done from.
	 * @param conn conection to commit.
	 * @throws SQLException on error.
	 */
	public void commit(final String fromWhere, final Connection conn) throws SQLException {
		if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE_CONNECTIONS) {
			long ts = System.currentTimeMillis();
			try {
				conn.commit();
				SQLOK(dparms, fromWhere, COMMIT, 0, ts, "commit", 0, null);
			} catch (SQLException se) {
				SQLOK(dparms, fromWhere, COMMIT, se.getErrorCode(), ts, COMMIT, 0, se);
				throw se;
			}
		}
		conn.commit();
	}

	/**
	 * Count the real character of string, mean if supplementary is count as one
	 * real character
	 *
	 * @param value
	 * @return number of real character
	 */
	private int countRealCharacter(String value) {
		if (value == null) {
			return 0;
		} else {
			int highSurogateChar = 0;
			char[] charArray = value.toCharArray();
			for (char charValue : charArray) {
				if (Character.isHighSurrogate(charValue)) {
					highSurogateChar++;
				}
			}
			return value.length() - highSurogateChar;

		}
	}

	/**
	 * Deletes a row from a passed ResultSet. Only exists as a separate method for reporting via diagnostics..
	 *
	 * @param rs a passed ResultSet.
	 * @throws SQLException on error.
	 */
	public void deleteRow(final ResultSet rs) throws SQLException {
		lastSQLOp = "Delete RS Row";
		lastSQL = lastSQLOp;
		if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			try {
				rs.deleteRow();
				if (trace(AppConfig.SQL_TRACE_UPDATES, ts)) {
					SQLOK(dparms, lastRoutine, lastSQLOp, 0, ts, lastSQL, 1, null);
				}
				return;
			} catch (SQLException se) {
				SQLOK(dparms, lastRoutine, lastSQLOp, se.getErrorCode(), ts, lastSQL, 0, se);
				throw se;
			}
		}
		rs.deleteRow();
	}

	/**
	 * Works out if a passed SQL statement is an insert, update, delete etc.
	 *
	 * @param aSql a passed SQL statement.
	 * @return type.
	 */
	public String deriveAccess(final String aSql) {
		if (aSql == null) {
			return "Unknown - SQL null";
		}
		String ucSql = aSql.substring(0, Math.min(10, aSql.length())).trim().toUpperCase();
		if (ucSql.startsWith("INSERT")) {
			return "Insert";
		} else if (ucSql.startsWith("UPDATE")) {
			return "Update";
		} else if (ucSql.startsWith("DELETE")) {
			return "Delete";
		} else if (ucSql.startsWith("SELECT")) {
			return "Select";
		} else {
			return "Complex";
		}
	}

	/**
	 * Do something if process finished normally; e.g. to commit pending modifications in case of transaction control is
	 * started.
	 */
	public void doFinal() {
		throw new UnsupportedOperationException("This method should be implemented by a subclass.");
	}

	/**
	 * Implements multilanguage support. Does nothing in the base class. Override it with your own requirements in the
	 * application extension.
	 *
	 * @param pScreen screen name.
	 * @return - the passed String containing the screen name, unchanged.
	 */
	public String doLanguage(final String pScreen) {
		return pScreen;
	}

	/**
	 * Clean up on exceptions e.g. rollback pending modifications if transaction control is started.
	 */
	public void doOnException() {
		throw new UnsupportedOperationException("This method should be implemented by a sub-class.");
	}

	/**
	 * Dumps accumulated SQL statistics. It is up to the application or the framework to call this method if and when
	 * required. At the end of a transaction or program is probably a good place.
	 */
	public void dumpSQLSummary() {
		if (sqlSummary == null) {
			return;
		}
		Enumeration ken = sqlSummary.keys();
		StringBuffer rep = new StringBuffer();
		String[] keyData = null;
		BaseData[] diagData = null;
		String cl = null;
		String me = null;
		String ln = null;
		DecimalData avg = new DecimalData(7, 3, 0, COMMA_SEP);

		String[] keys = new String[sqlSummary.size()];
		for (int i = 0; i < sqlSummary.size(); i++) {
			cl = (String) (ken.nextElement());
			diagData = (BaseData[]) sqlSummary.get(cl);
			me = QPUtilities.padRight(diagData[1].toString(), 10);
			keys[i] = me + cl;
		}
		try {
			Arrays.sort(keys, DESCENDING);
		} catch (Exception e) {
			doNothing();
		}
		int cw = 0;
		int mw = 0;
		for (int i = 0; i < sqlSummary.size(); i++) {
			if (keys[i].length() < 11) {
				keys[i] = keys[i] + "           ";
			}
			keyData = keys[i].substring(10).split(BP);
			cl = keyData.length > 1 ? keyData[1].trim() : BCLASSB;
			me = keyData.length > 2 ? keyData[2].trim() : BMETHODB;
			cw = cl.length() > cw ? cl.length() : cw;
			mw = me.length() > mw ? me.length() : mw;
		}
		cw++;
		mw++;
		rep.append("\nSQL Summary Report\n");
		rep.append(QPUtilities.padRight("Class", cw));
		rep.append(QPUtilities.padRight("Method", mw));
		rep.append(QPUtilities.padLeft("Line", 6));
		rep.append(Str.BK);
		rep.append(QPUtilities.padRight("Access", 10));
		rep.append(QPUtilities.padLeft("Total time", 12));
		rep.append(QPUtilities.padLeft("Calls", 7));
		rep.append(QPUtilities.padLeft("Average", 10));
		DecimalData totTime = new DecimalData(12, 3, 0, COMMA_SEP);
		int totCalls = 0;
		for (int i = 0; i < sqlSummary.size(); i++) {
			keyData = keys[i].substring(10).split(BP);
			diagData = (BaseData[]) sqlSummary.get(keys[i].substring(Num.I10));
			if (keyData == null || diagData == null) {
				continue;
			}
			cl = keyData.length > 1 ? keyData[1].trim() : BCLASSB;
			me = keyData.length > 2 ? keyData[2].trim() : BMETHODB;
			ln = keyData.length > 3 ? keyData[3] : "(Line)";
			if (ln.indexOf(COMP) > 0) {
				ln = COMP;
			}
			rep.append(Str.SYS_NL);
			rep.append(QPUtilities.padRight(cl, cw));
			rep.append(QPUtilities.padRight(me, mw));
			rep.append(QPUtilities.padLeft(ln, 6));
			rep.append(Str.BK);
			rep.append(QPUtilities.padRight(diagData[0].toString(), 10));
			rep.append(QPUtilities.padLeft(diagData[1].toString(), 12));
			rep.append(QPUtilities.padLeft(diagData[2].toString(), 7));
			avg.set(diagData[1]);
			avg.divide(diagData[2]);
			rep.append(QPUtilities.padLeft(avg.toString(), 10));
			totTime.add(diagData[1]);
			totCalls += diagData[2].toInt();
		}
		rep.append(Str.SYS_NL);
		rep.append(QPUtilities.padRight("Total", cw));
		rep.append(QPUtilities.padRight(Str.BK, mw));
		rep.append(QPUtilities.padLeft(Str.BK, 6));
		rep.append(Str.BK);
		rep.append(QPUtilities.padRight("All", 10));
		rep.append(QPUtilities.padLeft(totTime.toString(), 12));
		rep.append(QPUtilities.padLeft(totCalls + "", 7));
		avg.set(totTime);
		if (totCalls != 0) {
			avg.divide(totCalls);
		}
		rep.append(QPUtilities.padLeft(avg.toString(), 10));
		rep.append(Str.SYS_NL);
		addDiagnostic(rep.toString());
		sqlSummary.clear();
	}

	/**
	 * Available to end transaction control if implemented.
	 */
	public void endCommitControl() {
		throw new UnsupportedOperationException("Must be implemented in a subclass.");
	}

	/**
	 * Execute a piece of SQL and commits it immediately. This is for use when you are in the middle of a Managed
	 * Transaction, and you need to do some SQL that takes effect immediately, and which will be committed immediately.
	 * <p>
	 * The AppVars version is trivial, it just gets a connection and commits the results. This is, of course, not valid
	 * in a Managed Transaction.
	 * <p>
	 * Therefore, if you need to do it in a Managed Transaction, you should override the method in a subclass.
	 * <p>
	 * Ideas which may be useful for doing this include:
	 * <ol>
	 * <li>Do it in a separate EJB.
	 * <li>Start another Thread, and do it in that.
	 * <li>Use a Stored Procedure.
	 * <li>Obtain a non-managed connection from a separate Pool.
	 * </ol>
	 *
	 * @param sql SQL to be executed.
	 * @param parms parameters to resolve SQL parameter markers. Optional.
	 * @return number of rows affected.
	 * @throws SQLException on error.
	 */
	public int executeAndCommit(final String sql, final Object... parms) throws SQLException {
		Connection conn = getTempDBConnection(APPVARS);
		PreparedStatement ps = prepareStatement(conn, sql);
		Object o = null;
		try {
			for (int i = 0; parms != null && i < parms.length; i++) {
				o = parms[i];
				if (o == null || o instanceof StringBase || o instanceof String) {
					setDBString(ps, i + 1, o);
				} else if (o instanceof FastNIBase) {
					setDBNumber(ps, i + 1, (FastNIBase) o);
				} else if (o instanceof TimestampData) {
					setDBTimestamp(ps, i + 1, o);
				} else if (o instanceof RPGTimestampData) {
					setDBTimestamp(ps, i + 1, o);
				} else if (o instanceof DateData) {
					setDBDate(ps, i + 1, o);
				} else if (o instanceof RPGDateData) {
					setDBDate(ps, i + 1, o);
				} else if (o instanceof TimeData) {
					setDBTime(ps, i + 1, o);
				} else if (o instanceof RPGTimeData) {
					setDBTime(ps, i + 1, o);
				} else if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
					setDBInt(ps, i + 1, ((Number) o).intValue());
				} else if (o instanceof Long) {
					setDBLong(ps, i + 1, ((Long) o).longValue());
				} else if (o instanceof Double || o instanceof Short) {
					setDBDouble(ps, i + 1, ((Long) o).longValue());
				} else if (o instanceof Timestamp) {
					setDBTimestamp(ps, i + 1, o);
				} else if (o instanceof Date) {
					setDBDate(ps, i + 1, o);
				} else if (o instanceof TimeData) {
					setDBTime(ps, i + 1, o);
				} else if (o instanceof BigDecimal) {
					setDBBigDecimal(ps, i + 1, ((BigDecimal) o));
				} else {
					throw new RuntimeException(TYPE + o.getClass().getName() + CINDEX + i + NOT_CATERED);
				}
			}
			int ret = executeUpdate(ps);
			conn.commit();
			return ret;
		} finally {
			freeDBConnectionIgnoreErr(null, ps, null);
		}
	}

	/**
	 *
	 * @return boolean - return resultset or not
	 */
    public boolean executeAndCommitForTempTable(String sql, boolean isCommit) throws Exception {
    	Connection conn = getTempDBConnection(APPVARS);
		Statement stmt = conn.createStatement();
		try {
			boolean ret = executeStatement(stmt, sql);
			if (isCommit){
				conn.commit();
			}
			return ret;
		} catch (SQLException se) {
			throw se;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e1) {
				} catch (Throwable t1) {
				}
			}
			freeDBConnectionIgnoreErr(conn);
		}
    }

	/**
     * Execute DDL (Data Definition Language) queries <br>
     * <br>
     * DDL queries (CREATE, DROP, ALTER) are executed in another db connection.
     * These queries do not participate in transactions, thus changes are made
     * immediately with or without calling connection.commit().
     */
    public int executeDdl(String sql, Object... parms) throws SQLException {
        return executeAndCommit(sql, parms);
    }

	/**
	 * Execute a Select and return a ResultSet. Convenience method, use of this code assumes the following:
	 * <ol>
	 * <li>The PreparedStatement being used is the last one obtained through the prepareStatement call.
	 * <li>The call is being done from the same routine that the connection was obtained from, for the same piece of
	 * SQL.
	 * </ol>
	 * If these assumptions are not true, the SQL call may fail, and the reported place where the error occurred will be
	 * inaccurate.
	 *
	 * @param ps passed prepared SQL statement.
	 * @return ResultSet
	 * @throws SQLException on error.
	 */
	public ResultSet executeLockQuery(final PreparedStatement ps) throws SQLException {
		return executeLockQuery(lastRoutine, ps, lastSQL);
	}

	/**
	 * Execute a Select that is assumed to be a simple select for update and return a ResultSet after having fetched the
	 * first row. This code is separated out for convenience in tracing so that the timing and SQL codes are reported
	 * correctly. The reason is, the error if any occur on the fetch not on the SQL execution.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program.
	 * @param aPS the passed prepared statement.
	 * @param aSql the current SQL statement.
	 * @return ResultSet as a result of execution.
	 * @throws SQLException on error.
	 */
	public ResultSet executeLockQuery(final String fromWhere, final PreparedStatement aPS, final String aSql) throws SQLException {
		lastRoutine = fromWhere;
		lastSQL = aSql;
		lastSQLOp = "Lock";
		if (appConfig.getSqlDiagLevel() >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			ResultSet rs = null;
			try {
				rs = aPS.executeQuery();
				rs.next();
				if (trace(AppConfig.SQL_TRACE_QUERIES, ts)) {
					SQLOK(dparms, LOCK, deriveAccess(aSql), 0, ts, "Lock record", 1, null);
				}
			} catch (SQLException se) {
				SQLOK(dparms, fromWhere, LOCK, se.getErrorCode(), ts, "Lock record(e)", 0, se);
				throw se;
			}
			return rs;
		}
		ResultSet rs = aPS.executeQuery();
		rs.next();
		return rs;
	}

	/**
     * Execute queries on temporary tables <br>
     */
    public int executeOnTempTables(String sql, Object... parms) throws SQLException {
        if (!isCommitControlStarted()) {
            throw new RuntimeException(
                    "Commit control is not started yet, temporary tables must be updated in commit control.");
        }

        // Should return tx connection once commit control is started
        Connection conn = getDBConnection(APPVARS);

        return executeSql(conn, sql, parms);
    }

	/**
	 * Execute a Select and return a ResultSet. Convenience method, use of this code assumes the following:
	 * <ol>
	 * <li>The PreparedStatement being used is the last one obtained through the prepareStatement call.
	 * <li>The call is being done from the same routine that the connection was obtained from, for the same piece of
	 * SQL.
	 * </ol>
	 * If these assumptions are not true, the SQL call may fail, and the reported place where the error occurred will be
	 * inaccurate.
	 *
	 * @param ps the passed prepared statement.
	 * @return ResultSet
	 * @throws SQLException on error.
	 */
	public ResultSet executeQuery(final PreparedStatement ps) throws SQLException {
		return executeQuery(lastRoutine, ps, lastSQL);
	}

	/**
	 * Execute a Select and return a ResultSet.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program.
	 * @param aPS the passed prepared statement.
	 * @param aSql the current SQL statement.
	 * @return ResultSet
	 * @throws SQLException on error.
	 */
	public ResultSet executeQuery(final String fromWhere, final PreparedStatement aPS, final String aSql) throws SQLException {
		lastRoutine = fromWhere;
		lastSQL = aSql;
		lastSQLOp = "Execute Query";
		if (appConfig.getSqlDiagLevel() >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			ResultSet rs = null;
			try {
				rs = aPS.executeQuery();
				if (trace(AppConfig.SQL_TRACE_QUERIES, ts)) {
					SQLOK(dparms, fromWhere, deriveAccess(aSql), 0, ts, aSql, -1, null);
				}
			} catch (SQLException se) {
			    LOGGER.error("Error while executing SQL", se);
				SQLOK(dparms, fromWhere, deriveAccess(aSql), se.getErrorCode(), ts, aSql, -1, se);
				/* Find parm in error for update */
				if (se.getErrorCode() == MINUS_302 || se.getErrorCode() == MINUS_99999) {
					int ic = QPUtilities.count(aSql, "?");
                    LOGGER.error("executeQuery(String, PreparedStatement, String) - Detected " + se.getErrorCode() + " for " + ic + " parms.", se);
					for (int i = ic; i >= 1; i--) {
						try {
							aPS.setString(i, "0");
						} catch (Exception e) {
							try {
								aPS.setInt(i, 0);
							} catch (Exception e2) {
                                LOGGER.error("executeQuery(String, PreparedStatement, String) - Appvars - parm " + i + " could not be reset.", e2);
								continue;
							}
						}
						try {
							aPS.executeQuery();
                            LOGGER.error("executeQuery(String, PreparedStatement, String) - SQL ran after resetting parm " + i + ", so that's the first one in error.", se);
							break;
						} catch (SQLException se3) {
							// If we couldn't get the SQL to run, don't double-error.
							// We already reported the problem.
							doNothing();
						}
					}
				}
				throw se;
			}
			return rs;
		}
		return aPS.executeQuery();
	}

	/**
	 * See {@link #executeSimpleQuery(String, Connection, String, Object[])}. Equivalent to that method with the last
	 * parameter null.
	 *
	 * @param fromWhere see reference.
	 * @param conn see reference.
	 * @param aSql see reference.
	 * @return see reference.
	 * @throws SQLException see reference.
	 */
	public ResultSet executeSimpleQuery(final String fromWhere, final Connection conn, final String aSql) throws SQLException {
		return executeSimpleQuery(fromWhere, conn, aSql, null);
	}

	/**
	 * Execute a Select and return a ResultSet. Note, this requires the generation of an internal prepared statement,
	 * called tps. You can clean this up yourself, or it will be automatically cleaned up at the next execution of
	 * {@link #freeDBConnectionIgnoreErr(Connection, PreparedStatement, ResultSet)}. It will also be cleaned up in the
	 * next execution of this method.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. - name of
	 *            routine calling this method. Used for debugging purposes only. If null, the last provided valuie will
	 *            be used.
	 * @param aSql - Piece of SQL to be executed, can contain parameter markers.
	 * @param conn - Connection provided; can be null. If null, the Temp database connectino is used. This allows you to
	 *            decide if you want to use a particular connection, so that for example you see rows changed in the
	 *            current unit of work.
	 * @param parms - Parameters for the SQL. Must contain BaseData types only, and the type of BaseData must be
	 *            appropriate for the underlying parameter type, otherwise unexpected results may occur.
	 * @return - ResultSet
	 * @throws SQLException on error.
	 */
	public ResultSet executeSimpleQuery(final String fromWhere, Connection conn, final String aSql, final Object[] parms)
	        throws SQLException {

		lastSQLOp = "Execute Simple Query";
		ResultSet rs = null;
		if (conn == null) {
			conn = getTempDBConnection(fromWhere);
		}
		tps = prepareStatement(conn, aSql);
		setDBParameters(tps, parms);
		rs = executeQuery(tps);
		return rs;
	}

	/**
	 * See {@link #executeSimpleQuery(String, Connection, String, Object[])}. Equivalent to that method with the second
	 * and last parameters null.
	 *
	 * @param fromWhere see reference.
	 * @param aSql see reference.
	 * @return see reference.
	 * @throws SQLException see reference.
	 */
	public ResultSet executeSimpleQuery(final String fromWhere, final String aSql) throws SQLException {
		return executeSimpleQuery(fromWhere, aSql, null);
	}

	/**
	 * See {@link #executeSimpleQuery(String, Connection, String, Object[])}. Equivalent to that method with the second
	 * parameter null.
	 *
	 * @param fromWhere see reference.
	 * @param aSql see reference.
	 * @param parms see reference.
	 * @return see reference.
	 * @throws SQLException see reference.
	 */
	public ResultSet executeSimpleQuery(final String fromWhere, final String aSql, final Object[] parms) throws SQLException {
		return executeSimpleQuery(fromWhere, null, aSql, parms);
	}

	/**
	 * Execute and commit a passed piece of SQL.
	 *
	 * @param pConn connection to execute on.
	 * @param sql SQL to execute.
	 * @param parms for parameter markers.
	 * @param commit commit or not.
	 * @return number of rows affected.
	 * @throws SQLException on error.
	 */
	public int executeSimpleUpdate(final Connection pConn, final String sql, final BaseData[] parms, final boolean commit) throws SQLException {
		Connection conn = pConn;
		int ret = 0;
		if (conn == null) {
			conn = getTempDBConnection(APPVARS);
		}
		PreparedStatement ps = prepareStatement(conn, sql);
		try {
			BaseData bd;
			for (int i = 0; parms != null && i < parms.length; i++) {

				bd = parms[i];

				if (bd == null) {
					setDBString(ps, i + 1, null);
				} else if (bd instanceof StringBase) {
					setDBString(ps, i + 1, bd);
				} else if (bd instanceof FastNIBase) {
					setDBNumber(ps, i + 1, (FastNIBase) bd);
				} else if (bd instanceof TimestampData) {
					setDBTimestamp(ps, i + 1, (TimestampData) bd);
				} else if (bd instanceof RPGTimestampData) {
					setDBTimestamp(ps, i + 1, (RPGTimestampData) bd);
				} else if (bd instanceof DateData) {
					setDBDate(ps, i + 1, (DateData) bd);
				} else if (bd instanceof RPGDateData) {
					setDBDate(ps, i + 1, (RPGDateData) bd);
				} else if (bd instanceof TimeData) {
					setDBTime(ps, i + 1, (TimeData) bd);
				} else if (bd instanceof RPGTimeData) {
					setDBTime(ps, i + 1, (RPGTimeData) bd);
				} else {
					throw new RuntimeException(TYPE + bd.getClass().getName() + CINDEX + i + NOT_CATERED);
				}
			}
			ret = executeUpdate(ps);
			if (commit) {
				conn.commit();
			}
		} finally {
			freeDBConnectionIgnoreErr(null, ps, null);
		}
		return ret;
	}

	/**
	 * Execute a piece of update SQL and optionally commits it immediately. Note, this uses the temporary database
	 * connection. If it fails, this will rollback any open cursors on the temporary connection.
	 *
	 * @param sql - to be executed.
	 * @param parms - SQL parms
	 * @param commit - commit or not
	 * @return - number of rows affected
	 * @throws SQLException on error.
	 */
	public int executeSimpleUpdate(final String sql, final BaseData[] parms, final boolean commit) throws SQLException {
		return executeSimpleUpdate(null, sql, parms, commit);
	}
	/**
	 * Execute a Select and return the first row. It uses
	 * {@link #executeSimpleQuery(String, Connection, String, Object[])}.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. - name of
	 *            routine calling this method. Used for debugging purposes only.
	 * @param conn Connectino to execute on.
	 * @param aSql - Piece of SQL to be executed, can contain parameter markers.
	 * @param parms - Parameters for the SQL. It is expected that this contains either BaseData objects, or standard
	 *            Java Primitive Wrappers eg Integer. The type must be appropriate for the underlying parameter type,
	 *            otherwise unexpected results may occur.
	 * @param result - Array to be updated with the row fetched. If the current elements are BaseData types, their
	 *            contents will be updated. Otherwise the element(s) will be replaced with the result of a getObject
	 *            from the resultset, eg Integer.
	 * @return 0 row found, 100 no row found
	 * @throws SQLException on error.
	 */
	public int executeSingleRowQuery(final String fromWhere, final Connection conn, final String aSql, final Object[] parms, final Object[] result)
	        throws SQLException {
		ResultSet rs = executeSimpleQuery(fromWhere, conn, aSql, parms);
		boolean found = fetchNext(rs);
		if (found && result != null) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] instanceof BaseData) {
					((BaseData) result[i]).set(rs.getObject(i + 1));
				} else {
					result[i] = rs.getObject(i + 1);
				}
			}
		}
		freeDBConnectionIgnoreErr(null, tps, rs);
		return found ? 0 : 100;
	}

	/**
	 * Delegates to {@link #executeSingleRowQuery(String, Connection, String, Object[], Object[])} with null as the
	 * connection parm.
	 *
	 * @param fromWhere see delegate.
	 * @param aSql see delegate.
	 * @param parms see delegate.
	 * @param result see delegate.
	 * @return see delegate.
	 * @throws SQLException see delegate.
	 */
	public int executeSingleRowQuery(final String fromWhere, final String aSql, final Object[] parms, final Object[] result)
	        throws SQLException {
		return executeSingleRowQuery(fromWhere, null, aSql, parms, result);
	}

	public int executeSql(Connection conn, String sql, Object... parms) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = prepareStatement(conn, sql);

            for (int i = 0; parms != null && i < parms.length; i++) {
                setPreparedStatementParam(ps, parms[i], i);
            }

            return executeUpdate(ps);
        } finally {
        	if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

	/**
	 * Execute a non-Select SQL statement for Temp Table.
	 *
	 * @param stmt - previously statement.
	 * @param aSql
	 * @return boolean - return resultset or not
	 * @throws SQLException
	 */
	public boolean executeStatement(Statement stmt, String aSql) throws SQLException {
		try {
			return stmt.execute(aSql);
		} catch (SQLException se){
			throw se;
		}
	}

	/**
	 * Execute a non-Select SQL statement. Use of this code assumes the following:
	 * <ol>
	 * <li>The PreparedStatement being used is the last one obtained through the prepareStatement call.
	 * <li>The call is being done from the same routine that the connection was obtained from, for the same piece of
	 * SQL.
	 * </ol>
	 * If these assumptions are not true, the SQL call may fail, and the reported place where the error occurred will be
	 * inaccurate.
	 * <p>
	 * Delegates to {@link #executeUpdate(String, PreparedStatement, String)} with the cached {@link #lastRoutine} and
	 * {@link #lastSQL}.
	 *
	 * @param ps prepared statement ot execute.
	 * @return int - number of rows affected.
	 * @throws SQLException on error.
	 */
	public int executeUpdate(final PreparedStatement ps) throws SQLException {
		return executeUpdate(lastRoutine, ps, lastSQL);
	}

	/**
	 * Execute a non-Select SQL statement.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program.
	 * @param aPS the passed prepared statement. - previously prepared statement.
	 * @param aSql the current SQL statement.
	 * @return int - number of rows affected.
	 * @throws SQLException on error.
	 */
	public int executeUpdate(final String fromWhere, final PreparedStatement aPS, final String aSql) throws SQLException {
		lastRoutine = fromWhere;
		lastSQL = aSql;
		lastSQLOp = "Execute Update";
		if (appConfig.getSqlDiagLevel() >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			try {
				int rows = aPS.executeUpdate();
				if (trace(AppConfig.SQL_TRACE_UPDATES, ts)) {
					int rc = (rows == 0) ? 100 : 0;
					SQLOK(dparms, fromWhere, deriveAccess(aSql), rc, ts, aSql, rows, null);
				}
				return rows;
			} catch (SQLException se) {
				SQLOK(dparms, fromWhere, deriveAccess(aSql), se.getErrorCode(), ts, aSql, 0, se);
				throw se;
			}
		}
		return aPS.executeUpdate();
	}

	/**
	 * Position the ResultSet to the next row, with diagnositcs if required, convenience method.
	 * <p>
	 * Brief summary of convenience method purpose: Used where all SQL calls in the sequence get connection, prepare,
	 * execute, fetch are done in the one routine and method. Unsuitable if this is not true.
	 * <p>
	 * Detailed specification: Use of this code assumes the following:
	 * <ol>
	 * <li>The ResultSet being used is the one obtained through the last executeQuery call.
	 * <li>The call is being done from the same routine that the executeQuery was done in, or prepareStatement was done
	 * in if a similar convenience method was used to execute.
	 * </ol>
	 * If these assumptions are not true, the SQL call may fail, and the reported place where the error occurred will be
	 * inaccurate.
	 * <p>
	 * Delegates to {@link #fetchNext(String, ResultSet, String)} with the cached {@link #lastRoutine} and
	 * {@link #lastSQL}.
	 *
	 * @param rs passed result set to fetch next for.
	 * @return as for a normal result set fetch.
	 * @throws SQLException on error.
	 */
	public boolean fetchNext(final ResultSet rs) throws SQLException {
		return fetchNext(lastRoutine, rs, lastSQL);
	}

	/**
	 * Position the ResultSet to the next row, with diagnositcs if required.
	 * <p>
	 * The recommended standard is, do all fetches through the Framework for diagnostics and timing purposes.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. - name of
	 *            the routine performing the fetch (for diagnostic purposes only)
	 * @param rs - resultset to fetch from
	 * @param aSql - SQL that the fetch is for (for diagnostic purposes only)
	 * @return boolean - true=row was fetched
	 * @throws SQLException on error.
	 */
	public boolean fetchNext(final String fromWhere, final ResultSet rs, final String aSql) throws SQLException {
		lastRoutine = fromWhere;
		lastSQL = aSql;
		lastSQLOp = "Fetch";
		int l = 0;
		int s = 0;
		String s1 = "";
		String s2 = "";
		if (appConfig.getSqlDiagLevel() >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			boolean result = false;
			try {
				result = rs.next();
			} catch (SQLException se) {
				SQLOK(dparms, fromWhere, deriveAccess(aSql), se.getErrorCode(), ts, aSql, -1, se);
				throw se;
			}
			if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE_FETCH_DATA && appConfig.sqlSummaryOnly.equals("no")) {
				StringBuilder sb = new StringBuilder("\nRow fetched:");
				char c = ' ';
				if (result) {
					try {
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							sb.append(c);
							if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE_FETCH_DATA_MAX) {
								sb.append(' ');
								sb.append(rs.getMetaData().getColumnName(i));
								sb.append('=');
								String type = rs.getMetaData().getColumnTypeName(i);
								l = rs.getMetaData().getPrecision(i);
								s = rs.getMetaData().getScale(i);
								if (type.equals("CHAR")) {
									type = "C";
								} else if (type.startsWith("VARCHAR")) {
									type = "VC";
								} else if (type.startsWith("TIMESTAMP")) {
									type = "TS";
								} else if (type.equals("NUMBER") || type.equals("DECIMAL")) {
									type = "N";
								}
								sb.append(type);
								sb.append('(');
								sb.append(l);
								if (s > 0) {
									sb.append(',');
									sb.append(s);
								}
								sb.append(")='");
								Object o = rs.getObject(i);
								s1 = o == null ? "nuLL" : o.toString();
								s2 = QPUtilities.trimRight(s1);
								l = s1.length() - s2.length();
								if (l > 8) {
									s1 = "+" + l + 'b';
									sb.append(s2);
								} else {
									sb.append(s1);
									s1 = "";
								}
							} else if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE_FETCH_DATA_FULL) {
								sb.append(' ');
								sb.append(rs.getMetaData().getColumnName(i));
								sb.append("='");
								sb.append(QPUtilities.trimRight(rs.getObject(i), false));
							} else {
								sb.append(" '");
								sb.append(QPUtilities.trimRight(rs.getObject(i), false));
							}
							sb.append('\'');
							sb.append(s1);
							s1 = "";
							c = ',';
							if (appConfig.getSqlDiagLevel() < AppConfig.SQL_TRACE_FETCH_DATA_ALLCOLS
									&& sb.length() > 200) {
								break;
							}
						}
					} catch (Exception e) {
                        LOGGER.error("fetchNext(String, ResultSet, String)", e);
						sb.append(" " + e.toString());
					}
				} else {
					sb.append(" none.");
				}
				addDiagnostic(sb.toString());
			} else {
				if (trace(AppConfig.SQL_REPORT_FETCHES, ts)) {
					SQLOK(dparms, fromWhere, "Fetch", result ? 0 : Num.I100, ts, "Fetch", -1, null);
				}
			}
			return result;
		}
		return rs.next();
	}

	/**
	 * Auto fill the string with space
	 *
	 * @param originalString
	 * @param totalChacracter
	 *            total number character of string and spaces
	 * @return string is filled with spaces has total length equal
	 *         totalCharacter < parameter
	 */
	private String fillSpace(String originalString, int totalChacracter) {
		int spaceNum = totalChacracter - originalString.length();
		if (spaceNum > 0) {
			char[] spaceChar = new char[spaceNum];
			Arrays.fill(spaceChar, ' ');
			String newString = originalString + new String(spaceChar);
			return newString;
		} else {
			return originalString;
		}
	}


	/**
	 * Added by Max W. Content moved from StatefulSessionFacadeBean.cleanup(), to ensure it will be executed This method
	 * is supposed to deal with clean up attributes holding by the AppVars. It could be overriden, super.finalize() must
	 * be ensured to be called. This method is only supposed to be invoked manually when a session becomes invalid.
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
		try {
			finalizeVars();
		} catch (Throwable e1) {
			addStaticDiagnostic(e1.toString());
		}finally {
			/*try {
	            super.finalize();
            } catch (Throwable e2) {
            	addStaticDiagnostic(e2.toString());
            }*/
		}
	}

	/**
	 * Cleanup variables;
	 */
	public void finalizeVars() {
		finalised = true;
	}

	/**
	 * finally free all Appvars connections.
	 * It will free all connections in Appvars and its supclasses.
	 * Subclasses should overwrite this method to free their connection in case they have
	 */
	public void finallyFreeAllAppVarsConnections() {
        // free all connection in AppVars and its sub classes
	    // txConnection, qtempConnection, otherconn are in subclass SMARTAppVars

    }

	/**
	 * An overridable method to free database connections. This is available for applications where freeing the database
	 * connection doesn't really do that as the connection is a pooled one, and the free at the end of SQL calls will be
	 * ignored. This method would then be called onapplication exit.
	 *
	 * @param conn connection to free.
	 */
	public void finallyFreeDBConnectionIgnoreError(final Connection conn) {
		throw new RuntimeException(
		    "Can't call finallyFreeDBConnectionIgnoreError in AppVars; must use application extender! " + conn);
	}

	/**
	 * In this code, delegates to {@link #reallyFreeDBConnection(Connection)}. In applications, due to connection
	 * pooling or the use of singleton connections, this method might be overridden. In that case, on application exit,
	 * {@link #finallyFreeDBConnectionIgnoreError(Connection)} would be used.
	 *
	 * @param conn connection to free.
	 * @throws Exception on error.
	 */
	public void freeDBConnection(final Connection conn) throws Exception {
		reallyFreeDBConnection(conn);
	}

	/**
	 * Delegates to {@link #freeDBConnection(Connection)} but ignores any error.
	 *
	 * @param conn connection to free.
	 */
	public void freeDBConnectionIgnoreErr(final Connection conn) {
		try {
			freeDBConnection(conn);
		} catch (Exception e) {
			doNothing();
		}
	}

	/**
	 * Method freeDBConnectionIgnoreErr.
	 *
	 * @param conn connection to free. It may be null, in which case no freeing will be attempted. The work is done by
	 *            {@link #freeDBConnectionIgnoreErr(Connection)}.
	 * @param aPS the passed prepared statement to free. It may be null. If not, it will be closed. Any error will be
	 *            ignored.
	 * @param aResultSet the passed result set to free. It may be null. If not, it will be closed. Any error will be
	 *            ignored.
	 */
	public void freeDBConnectionIgnoreErr(final Connection conn, final Statement aPS, final ResultSet aResultSet) {
		if (aPS != null) {
			try {
				aPS.close();
			} catch (Exception e1) {
				// Cannot imagine why unable to free. Ignore the error as we might be
				// in a free connection because of error, leading to a loop.
				doNothing();
			} catch (Throwable t1) {
				// Cannot imagine why unable to free. Ignore the error as we might be
				// in a free connection because of error, leading to a loop.
				doNothing();
			}
		}
		if (tps != null) {
			try {
				tps.close();
				tps = null;
			} catch (Exception e1) {
				// Cannot imagine why unable to free. Ignore the error as we might be
				// in a free connection because of error, leading to a loop.
				doNothing();
			}
		}
		if (aResultSet != null) {
			try {
				aResultSet.close();
			} catch (Exception e1) {
				// Cannot imagine why unable to free. Ignore the error as we might be
				// in a free connection because of error, leading to a loop.
				doNothing();
			} catch (Throwable t1) {
				// Cannot imagine why unable to free. Ignore the error as we might be
				// in a free connection because of error, leading to a loop.
				doNothing();
			}
		}
		freeDBConnectionIgnoreErr(conn);
	}

	/**
	 * Routine available to free any resources eg caches. Does nothing in this implementation.
	 *
	 * @param exceptFullClassNames an array of resources that will (conceptually) not be freed.
	 */
	public void freeResources(final String... exceptFullClassNames) {
	}

	/**
	 * Delete any temporary tables
	 */
	public void freeTemporaryTables() {
		throw new RuntimeException("Can't call freeTemporaryTables in AppVars; must use application extender! ");
	}

	/**
	 * Getter for {@link #activeField}.
	 *
	 * @return {@link #activeField}.
	 */
	public String getActiveField() {
		return activeField;
	}

	/**
	 * An effectively abstract method to get a Database Connection. You cannot call the version in AppVars, it gives an
	 * error message; it should be overridden in a superclass. It exsits to support cases where two connections are
	 * required. An example of this is the following: an application needed to do "dirty reads" on one connection so
	 * that data could always be obtained, on cursors that would not close when a lock failed. Updates were done on the
	 * other one, and could fail.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. -
	 *            diagnostic String saying from what code this SQL statement was called.
	 * @return Another Database Connection.
	 * @throws SQLException on error.
	 */
	public Connection getAnotherDBConnection(final String fromWhere) throws SQLException {
		throw new RuntimeException(fromWhere
		        + " - Can't call getAnotherDBConnection in AppVars; must use application extender!");
	}

	/**
	 * Returns the application configuration. This method should always be used rather than storing a local copy.
	 *
	 * @return {@link #appConfig}.
	 */
	public AppConfig getAppConfig() {
		return appConfig;
	}

	/**
	 * Returns the Application Name. This is obtained by the Servlet that handles the application, which should obtain
	 * it as a parameter from the Web Container.
	 *
	 * @return Application Name
	 */
	public String getApplicationName() {
		return appName;
	}

    public String getApplicationUse0() {
	        return ApplicationUse0;
	    }

	public String getApplicationUse1() {
        return ApplicationUse1;
    }

	public String getApplicationUse10() {
        return ApplicationUse10;
    }

	public String getApplicationUse11() {
        return ApplicationUse11;
    }

	public String getApplicationUse12() {
        return ApplicationUse12;
    }

	public String getApplicationUse2() {
        return ApplicationUse2;
    }

	public String getApplicationUse3() {
        return ApplicationUse3;
    }

	public String getApplicationUse4() {
        return ApplicationUse4;
    }

	public String getApplicationUse5() {
        return ApplicationUse5;
    }

	public String getApplicationUse6() {
        return ApplicationUse6;
    }

	public String getApplicationUse7() {
        return ApplicationUse7;
    }

	public String getApplicationUse8() {
        return ApplicationUse8;
    }

	public String getApplicationUse9() {
        return ApplicationUse9;
    }

	public String getBusinessdate() {
		return businessdate;
	}

	public boolean getBusinessDateFlag() {
		return businessDateFlag;
	}

	/**
	 * Returns the checkedForResume.
	 *
	 * @return boolean
	 */
	public int getCheckedForResume() {
		return checkedForResume;
	}

	/**
	 * Returns the collateChar.
	 *
	 * @return String
	 */
	public String getCollateChar() {
		return collateChar;
	}

	// add getBusinessDateFlag and setBusinessDateFlag for signing the businessdate modified(xma3 2009-08-21)
	public String[][] getCompanyBranchArray() {
		return companyBranchArray;
	}

	/**
     * Getter for {@link #cursorField}.
     *
     * @return {@link #cursorField}.
     */
	public String getCursorField() {
		return cursorField;
	}

    /**
	 * An effectively abstract method to get a Database Connection. You cannot call the version in AppVars, it gives an
	 * error message; it should be overridden in a superclass.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. -
	 *            diagnostic String saying from what code this SQL statement was called.
	 * @return A Database Connection.
	 * @throws SQLException on error.
	 */
	public Connection getDBConnection(final String fromWhere) throws SQLException {
		throw new RuntimeException(fromWhere
		        + " - Can't call getDBConnection in AppVars; must use application extender!");
	}

    /**
	 * A general purpose routine to allow interaction with the Database to retrieve information. Functionality is
	 * completely dependent on the application and the implementing database.
	 *
	 * @param typeOfInfo - a nominal variable to indicate the type of information being looked for, eg "IDENTITYCOUMNS",
	 *            "NEXT WEEKS LOTTO NUMBERS".
	 * @param lookFor - a nominal variable to indicate the value being looked for.
	 * @return An Oject whose contents is completely context dependent.
	 */
	public Object getDBInfo(final Object typeOfInfo, final Object lookFor) {
		throw new RuntimeException(typeOfInfo + Str.BK + lookFor
		        + " Can't call getDBInfo in AppVars; must use application extender!");
	}

	/**
	 * Populate result set into specified fields. If the no of fields is greater than the no of columns, then clear the
	 * rest.
	 *
	 * @param rs passed result set under consideration.
	 * @param resultFields array of variables to store the results.
	 * @throws SQLException on error.
	 */
	public final void getDBObject(final ResultSet rs, final BaseData... resultFields) throws SQLException {
		if (rs == null || resultFields == null || resultFields.length == 0) {
			return;
		}
		int columnCount = rs.getMetaData().getColumnCount();
		for (int i = 0; i < resultFields.length; i++) {
			BaseData resultField = resultFields[i];
			int dbIndex = i + 1;
			if (columnCount < dbIndex) {
				resultField.clear();
			} else {
				getDBObject(rs, dbIndex, resultField);
			}
		}
	}

    /**
	 * Get a value from db to result field specified. If there is no such an field, then clear result field.
	 * @param rs passed result set under consideration.
	 * @param dbIndex column position.
	 * @param resultField variable to store the result in.
	 * @return a Integer that refers to Null Indicator value.
	 * @throws SQLException on error.
	 */
	public final int getDBObject(final ResultSet rs, final int dbIndex, final BaseData resultField) throws SQLException {
		return FrameworkDialectFactory.getInstance().getAppVarsSupport().getDBObjectSupport(rs, dbIndex, resultField);


	}

    /**
	 * Overloaded getDBObject(...) method for handling Null-Indicator.
	 * It does the same thing as {@link #getDBObject(ResultSet, int, BaseData)},
	 * but set an integer to nullIndicator if it's specified.
	 * @param rs - @see {@link #getDBObject(ResultSet, int, BaseData)}
	 * @param dbIndex - @see {@link #getDBObject(ResultSet, int, BaseData)}
	 * @param resultField - @see {@link #getDBObject(ResultSet, int, BaseData)}
	 * @param nullIndicator - BaseData to carry null indicator integer,
	 * 							which can be 0 (normal)/-1(null value)/-2 (trucated value)
	 * @throws SQLException - on any db error
	 */
	public void getDBObject(final ResultSet rs, final int dbIndex, final BaseData resultField,
			final BaseData nullIndicator) throws SQLException {

		final int nullInd = getDBObject(rs, dbIndex, resultField);
		if (nullIndicator != null) {
			// Setting Null Indicator value to nullIndicator Field.
			nullIndicator.set(nullInd);
		}
	}

	/**
	 * A convenient method to get a String from ResultSet with character set converted.
	 *
	 * @param rs passed result set under consideration.
	 * @param index column position.
	 * @return a String extracted from the result set at the nominated index.
	 * @throws SQLException on error.
	 */
	public final String getDBString(final ResultSet rs, final int index) throws SQLException {
		//UTF-8 related Change
		return (String)rs.getString(index);
	}

	/**
	 * Getter for {@link #debugAllowed}.
	 *
	 * @return {@link #debugAllowed}.
	 */
	public int getDebugAllowed() {
		return debugAllowed;
	}

	/**
	 * Method getDebuggingField. Gets a named field from this Class if public. Ignores any error.
	 *
	 * @param f name of field to find.
	 * @return Object found thing.
	 * @throws Exception well, not really.
	 */
	public Object getDebuggingField(final String f) throws Exception {
		try {
			return this.getClass().getField(f).get(this);
		} catch (Exception e) {
			// Only a debugging field. Better to ignore the error than crash.
			// Never used in production anyway.
			doNothing();
		}
		return null;
	}


	/**
	 * Returns the list of diagnostics in this Application.
	 *
	 * @return A MessageList object containing the messages.
	 */
	public MessageList getDiagnostics() {
		return diagnostics;
	}

	/**
	 * Getter for {@link #dparms}.
	 *
	 * @return {@link #dparms}.
	 */
	public Object[] getDparms() {
		Object[] copy = dparms; // Copy to avoid error message.
		return copy;
	}


	/**
	 * Method getFieldHelp. Returns fieldhelp from the Quipoz fieldhelp table via the GENFieldHelp module. When used at
	 * a client site this may be used unchanged, or, if this is for a converted application then a different version can
	 * be called in that application's extension of AppVars.
	 *
	 * @param screen name of screen.
	 * @param field name of field help requested for.
	 * @return String help text.
	 */
	public String getFieldHelp(final String screen, final String field) {
		return GENFieldHelp.getHelp(this, screen, field);
	}

	/**
	 * Checks to see if a field is defined in this class. Does NOT require the field to be public.
	 *
	 * @param v1 name of the field required.
	 * @return String value of the field.
	 */
	public String getGlobalField(String v1) {
		if (v1 == null) {
			return "";
		}

		String[] sa = QPUtilities.split(v1, ".");
		if (!sa[1].equals("")) {
			v1 = sa[1];
			if (!sa[0].toUpperCase().startsWith("GLOB")) {
				return "";
			}
		}

		/* Look in this class for any such field */
		try {
			return this.getClass().getDeclaredField(v1).get(this) + "";
		} catch (Exception e) {
			// Handled in the next block.
			doNothing();
		}

		/*
		 * OK, not there. It might be "protected" in a superclass. Note, can't get it if its private, and will have got
		 * it above if its public.
		 */
		Class cl = this.getClass().getSuperclass();
		while (cl != null) {
			try {
				Field f = cl.getDeclaredField(v1);
				if (Modifier.isProtected(f.getModifiers()) || Modifier.isPublic(f.getModifiers())) {
					return f.get(this) + "";
				}
				if (Modifier.isPrivate(f.getModifiers())) {
					return "";
				}
			} catch (Exception e) {
				cl = cl.getSuperclass();
			}
		}
		return "";
	}

	// add the getBusinessdate for displaying the current businessdate in the home page (xma3 2009-08-21)

	/**
	 * Getter for {@link #heartBeat}.
	 *
	 * @return {@link #heartBeat}.
	 */
	public long getHeartBeat() {
		return heartBeat;
	}
	/**
	 * An effectively abstract method to get a Database Connection outside of Transaction scope. You cannot call the
	 * version in AppVars, it gives an error message; it should be overridden in a superclass. It exsits to support
	 * cases where some SQL needs to be run within a Managed Transaction, but committed immediately. An example might be
	 * the need to use an arbitrary and unknown (at compiler time) schema, but a table with a particular name must exist
	 * in that schema. The application checks if that table exists, and if not, creates it. But we want that table to be
	 * available to others immediately, not when this transaction ends. Trust me on this, this sort of thing is often
	 * required.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. -
	 *            diagnostic String saying from what code this SQL statement was called.
	 * @return A Database Connection.
	 * @throws SQLException on error.
	 */
	public Connection getImmediateDBConnection(final String fromWhere) throws SQLException {
		throw new RuntimeException(fromWhere
		        + " - Can't call getImmediateDBConnection in AppVars; must use application extender!");
	}

    public String[] getLanguageArray() {
		return languageArray;
	}

	/**
	 * * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * Returns the loggedOnUser.
	 *
	 * @return String
	 */
	public String getLoggedOnUser() {
		return loggedOnUser;
	}

	/**
	 * Returns the logonNextScreen.
	 *
	 * @return String
	 */
	public String getLogonNextScreen() {
		return logonNextScreen;
	}

	public HashMap<String, String> getMasterMenu() {
		return masterMenu;
	}
	
	public HashMap<String, String> getMasterOtherMenu() {
		return masterOtherMenu;
	}
	/**
	 * Returns the list of messages in this Application.
	 *
	 * @return A MessageList object containing the messages.
	 */
	public MessageList getMessages() {
		return messages;
	}

	/**
	 * Returns the list of new messages in this Application.
	 *
	 * @return A String object containing the messages.
	 */
	public String getNewMessages() {
		fieldErrorsExist = false;
		return messages.toStringNew();
	}

	/**
	 * Method getNextAction. Gets the next action to be processed, on screen resumption.
	 *
	 * @return String
	 */
	public String getNextAction() {
		return nextAction;
	}

	/**
	 * Getter for {@link #nextScreen}.
	 *
	 * @return {@link #nextScreen}.
	 */
	public String getNextScreen() {
		return nextScreen;
	}

	/**
	 * Getter for {@link #nextScrSm}.
	 *
	 * @return {@link #nextScrSm}.
	 */
	public ScreenModel getNextScrSm() {
		return nextScrSm;
	}

	/**
	 * Obtain the next sequence number from a sequence table. A sequence table is one that is defined in the relational
	 * database being used to implement the application, for the purpose of generating unique ascending numbers.
	 * Typically, this is done with a "create sequence" statement.
	 * <p>
	 * The code to get the next number differs from one database implementation to the next, and so should be overridden
	 * at the level of AppVars where database dependent code is implemented.
	 * <p>
	 * It is intended that getting the number doesn't take a lock, and is committed immediately. Whether or not this is
	 * possible depends on the Transaction Management system employed, so it may need to be implemented at an even
	 * higher level.
	 *
	 * @param seqTable - Sequence table to get the long from
	 * @return a number
	 */
	public long getNextSequenceNo(final String seqTable) {
		throw new RuntimeException("Can't call getNextSequenceNo in AppVars; must use application extender "
		        + "that implements for a particular type of database!");
	}

	/**
	 * Returns the nextSingleModelJSPScreen.
	 *
	 * @return String
	 */
	public String getNextSingleModelJSPScreen() {
		return nextSingleModelJSPScreen;
	}

	/**
	 * Getter for {@link #popup}.
	 *
	 * @return {@link #popup}.
	 */
	public PopupArrayList getPopup() {
		return popup;
	}

	/**
	 * Getter for the menu from {@link #popup}, see {@link PopupArrayList#getMenu()}.
	 *
	 * @return {@link #popup}'s menu.
	 */
	public boolean getPopupMenu() {
		return popup.getMenu();
	}

	/**
	 * Getter for {@link #popupType}.
	 *
	 * @return {@link #popupType}.
	 */
	public String getPopupType() {
		return popupType;
	}

	/**
	 * Class to resolve a program according to the application's class path. The base AppVars version does nothing
	 * except throw an error, in order to function a higher level class needs to be used, eg at application level.
	 *
	 * @param progname program to be resolved.
	 * @return resolved program.
	 */
	public Object getProgram(final Object progname) {
		throw new RuntimeException(progname + " - Cannot use the AppVars version of getProgram.");
	}

	/**
	 * @return the resourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Returns the screenEntry.
	 *
	 * @return long
	 */
	public long getScreenEntry() {
		return screenEntry;
	}

	/**
	 * Returns the select statement to be prepared. Selection is based on base table name and base schema. Result set
	 * either empty or not.
	 *
	 * @return String
	 */
	public String getSelectDependentViewsSql() {
		return getSimpleDbDialect().getSelectDependentViewsSql();
	}

	public String getSelectDependentViewsMySql() {
		throw new RuntimeException(
				"Can't call getSelectDependentViewsMySql in AppVars; must use application extender!");
	}

	/**
	 * Returns the select statement to be prepared. Selection is based on view name and view schema. Result set will
	 * contain definition CLOB and schema.
	 *
	 * @return String
	 */
	public String getSelectViewDefinitionSql() {
		return getSimpleDbDialect().getSelectViewDefinitionSql();
	}

	/**
	 * Returns the select statement to be prepared. Selection is based on dependent view name and dependent view schema.
	 * Result set will contain base schemas and table names
	 *
	 * @return String
	 */
	public String getSelectViewSchemaFromSystemCatalogSql() {
		return getSimpleDbDialect().getSelectViewSchemaFromSystemCatalogSql();
	}

	/**
	 * Getter for {@link #sessionId}.
	 *
	 * @return {@link #sessionId}.
	 */
	public String getSessionId() {
		return sessionId;
	}

	public String getSubMenu() {
		return  subMenu;

	}

	public SimpleDbDialect getSimpleDbDialect() {
		if (simpleDbDialect == null) {
			SimpleDbDialectManager.setDialect(DataSourceUtils.getDatabaseName(getAppConfig().getDataSourceJDBC()));
			simpleDbDialect = SimpleDbDialectManager.getSimpleDialect();
		}
		return simpleDbDialect;
	}

	/**
	 * Getter for {@link #supportPath}.
	 *
	 * @return {@link #supportPath}.
	 */
	public String getSupportPath() {
		return supportPath;
	}

	public Object[] getSystemMenu() {
		return systemMenu;
	}
	
	public Object[] getSystemOtherMenu() {
		return systemOtherMenu;
	}
	
	public String getOtherMenuActive() {
		return otherMenuActive;
	}

	/**
	 * Method getTableName. Adds the application DBSchema to the table name, i.e qualifies it.
	 *
	 * @param tableName table to be qualified.
	 * @return in this version, {@link AppConfig#getDBSchema()} + '.' + uppercase(passed tablename).
	 */
	public String getTableName(final String tableName) {
		return appConfig.getDBSchema() + "." + tableName.toUpperCase();
	}

	/**
	 * Convenience method equivalent to {@link AppConfig#getDBSchema() getAppConfig().getDBSchema}. This could be
	 * overridden in a subclass to do something else.
	 *
	 * @return {@link AppConfig#getDBSchema() getAppConfig().getDBSchema}.
	 */
	public String getTableSchema() {
		return appConfig.getDBSchema();
	}

	/**
	 * An effectively abstract method to get a Database Connection. You cannot call the version in AppVars, it gives an
	 * error message; it should be overridden in a superclass. It exsits to support cases where a temporary database
	 * connection is required. An example of this is where Global Tempporary Tables are used. You cannot do a commit on
	 * them as everything disappears.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. -
	 *            diagnostic String saying from what code this SQL statement was called.
	 * @return A temporary Database Connection.
	 * @throws SQLException on error.
	 */
	public Connection getTempDBConnection(final String fromWhere) throws SQLException {
		throw new RuntimeException(fromWhere
		        + " - Can't call getTempDBConnection in AppVars; must use application extender!");
	}

	/**
	 * Retuns the list of screen timings.
	 *
	 * @return {@link #timings}.
	 */
	public MessageList getTimings() {
		return timings;
	}

	/**
	 * Returns the transferArea.
	 *
	 * @return String
	 */
	public String getTransferArea() {
		return transferArea;
	}

	/**
	 * Getter for {@link #loggedOnUser}. Returns {@link #UNKNOWN} if the value is null or empty.
	 *
	 * @return enhanced {@link #loggedOnUser}.
	 */
	public String getUser() {
		if (loggedOnUser == null || loggedOnUser.trim().length() == 0) {
			return UNKNOWN;
		}
		return loggedOnUser;
	}

	/**
	 * @return the userDescription
	 */
	public String getUserDescription() {
		return userDescription;
	}

	/**
     * @param userDescription the userDescription to set
     */
    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

	/**
	 * Getter for {@link #loggedOnUserLanguage}.
	 *
	 * @return {@link #loggedOnUserLanguage}.
	 */
	public FixedLengthStringData getUserLanguage() {
		return loggedOnUserLanguage;
	}

	/**
	 * Checks to see if a field is accessable in this class. Checks the top level class for a declared field
	 * (private/protected/public), then the superclasses for protected.
	 *
	 * @param v1 name of a field.
	 * @return true if it is accessible.
	 */
	public boolean globalField(String v1) {
		if (v1 == null) {
			return false;
		}

		String[] sa = QPUtilities.split(v1, ".");
		if (!sa[1].equals("")) {
			v1 = sa[1];
			if (!sa[0].toUpperCase().startsWith("GLOB")) {
				return false;
			}
		}

		/* Look in this class for any such field */
		try {
			return this.getClass().getDeclaredField(v1).get(this) != null;
		} catch (Exception e) {
			// Handled in the next block.
			doNothing();
		}

		/*
		 * OK, not there. It might be "protected" or "public" in a superclass. Note, can't get it if its private, and
		 * will have got it above if its public.
		 */
		Class cl = this.getClass().getSuperclass();
		while (cl != null) {
			try {
				Field f = cl.getDeclaredField(v1);
				return Modifier.isProtected(f.getModifiers()) || Modifier.isPublic(f.getModifiers());
			} catch (Exception e) {
				cl = cl.getSuperclass();
			}
		}
		return false;
	}

	/**
	 * Returns whther the Screen has messages to be displayed.
	 *
	 * @return A boolean value indicating presence of messages.
	 */
	public boolean hasMessages() {
		return (messages != null) && (messages.size() > 0);
	}

	/**
	 * Returns true if {@link #popup}.size() &gt; 0.
	 *
	 * @return true if {@link #popup}.size() &gt; 0.
	 */
	public boolean hasPopup() {
		return (popup.size() > 0);
	}

	/**
	 * Initialise any global variables. No action, but it may be overridden in a superclass.
	 */
	public void initVariables() {
	}

	protected boolean isAppendingSuffix() {
		return true;
	}

	/**
	 * Check if commitment control is started.
	 *
	 * @return true if it is.
	 */
	public boolean isCommitControlStarted() {
		// This convoluted code so that we can test the method without FindBugs telling us
		// that it always throws an unsupported exception.
		int v1 = 1;
		UnsupportedOperationException us = new UnsupportedOperationException(
		    "This method must be implemented in subclass.");
		if (v1 == 1) {
			throw us;
		} else {
			return true;
		}
	}

	/**
	 * Check if diagnostic is enabled for default debugging level.
	 *
	 * @return
	 */
	public boolean isDiagnosticEnable() {
		return isDiagnosticEnable(AppConfig.DEFAULT_DEBUG_LEVEL);
	}

	/**
	 * Check if diagnostic is enable for specific debugging level.
	 *
	 * @param level
	 * @return
	 */
	public boolean isDiagnosticEnable(final int level) {
		return getAppConfig() != null && level <= getAppConfig().debugLevel;
	}

	/**
	 * Returns the loggedOn.
	 *
	 * @return boolean
	 */
	public boolean isLoggedOn() {
		return loggedOn;
	}

	/**
	 * Returns the logonRequired.
	 *
	 * @return boolean
	 */
	public boolean isLogonRequired() {
		return logonRequired;
	}

	public boolean isMenuDisplayed() {
		return isMenuDisplayed ;
	}

	/**
	 * Getter for {@link #nextScrXCTL}.
	 *
	 * @return {@link #nextScrXCTL}.
	 */
	public boolean isNextScreenXCTL() {
		return nextScrXCTL;
	}

	/**
	 * Adds text to {@link #updateLog} from the passed result set and the passed column number within the result set. If
	 * the column number is 1, updateLog is cleared first. String values will be put in apostrophes. A typical result
	 * might be that " ,CUSTNO='314159'" gets appended to updateLog.
	 *
	 * @param rs the passed result set.
	 * @param parmno the passed column number within the result set.
	 */
	private void logParm(final ResultSet rs, final int parmno) {
		if (getAppConfig().getSqlDiagLevel() < AppConfig.SQL_TRACE_UPDATES) {
			return;
		}
		if (parmno == 1) {
			updateLog.setLength(0);
		} else {
			updateLog.append(", ");
		}
		try {
			updateLog.append(rs.getMetaData().getColumnName(parmno));
			updateLog.append("=");
			Object value = rs.getObject(parmno);
			if (value == null) {
				updateLog.append("(null)");
			} else if (value instanceof Number) {
				updateLog.append(((Number) value).toString());
			} else if (value instanceof String) {
				updateLog.append(Str.APOST + value + Str.APOST);
			} else if (value instanceof Date) {
				updateLog.append(Str.APOST + value + Str.APOST);
			} else if (value instanceof Time) {
				updateLog.append(Str.APOST + value + Str.APOST);
			} else if (value instanceof Timestamp) {
				updateLog.append(Str.APOST + value + Str.APOST);
			} else {
				updateLog.append(value.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a clone of the application variables area. Note that this should also be present in the extended version
	 * created for the implemented application.
	 * <p>
	 * <b>NOTE</b> this clone method <b><u><i>does not</i></u></b>
	 * <ol>
	 * <li>Clone the messages
	 * <li>Clone the timings
	 * <li>Clone diagnostics
	 * <li>Clone alerts
	 * <li>Clone popups
	 * </ol>
	 * <p>
	 * Those areas are initialised to empty. The reason is, the clone method is to create a new AppVArs to be passed,
	 * probably across a remote interface, which will come back containing <u>new</u> messages etc which should be
	 * copied back <u>in addition</u> to the existing messages.
	 *
	 * @return clone of AppVars.
	 */
	public AppVars mediumClone() {
		AppVars result;
		try {
			result = (AppVars) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e.toString());
		}
		result.messages = new MessageList();
		result.diagnostics = new MessageList();
		result.timings = new MessageList();
		result.alerts = new StringBuffer();
		result.popup = new PopupArrayList();
		return result;
	}

	/**
	 * Pad whitespace to string with the length is given and adjust length of
	 * string if they contain supplementary character. Whitespace will be add to
	 * the right on every supplementary cdharacter was counted.
	 *
	 * @param value
	 * @param length
	 *            length of string (including whitespace to pad)
	 * @return string was adjusted length for supplementary character
	 */
	private String padRightAndAdjustSupplementaryChars(String value, int length) {
		String str = value;
		if (str.length() > length) {
			return subSupplementaryChars(str, length);
		}

		int realCharacter = countRealCharacter(str);
		if (realCharacter < value.length()) {
			int totalCharacter = length + (value.length() - realCharacter);
			str = fillSpace(str, totalCharacter);
		} else {
			str = fillSpace(str, length);
		}

		return str;
	}

	/**
	 * Formats the last part of a diagnostic message about an executed SQL statement. This includes the time it took
	 * place, and the module concerned.
	 *
	 * @return A Diagnostic String.
	 */
	private String postamble() {
		if (appConfig.traceSource.equals("detail")) {
			lastPreamble = QPUtilities.getCurrentMethodNameNot(dontReport);
			String s = " : " + lastPreamble + " <longtext>"
			        + QPUtilities.getMethodsBelow("AppVars/ApplicationVariables") + "</longtext>";
			return s;
		} else if (appConfig.traceSource.equals("on")) {
			lastPreamble = QPUtilities.getCurrentMethodNameNot(dontReport);
			return " : " + lastPreamble + Str.BK;
		} else if (appConfig.traceSource.equals("low")) {
			lastPreamble = QPUtilities.getCallerNot(dontReport);
			return " : " + lastPreamble + Str.BK;
		} else {
			return "  ";
		}
	}

	/**
	 * Formats the first part of a diagnostic message about an executed SQL statement. This includes the time it took
	 * place, and the module concerned.
	 *
	 * @return A diagnostic String.
	 */
	private String preamble() {
		if (appConfig.traceSource.equals("detail")) {
			lastPreamble = QPUtilities.getCurrentMethodNameNot(dontReport);
			String s = QPUtilities.logStamp() + Str.BK + lastPreamble + " <longtext>"
			        + QPUtilities.getMethodsBelow("AppVars/ApplicationVariables") + "</longtext>";
			return s;
		} else if (appConfig.traceSource.equals("on")) {
			lastPreamble = QPUtilities.getCurrentMethodNameNot(dontReport);
			return QPUtilities.logStamp() + Str.BK + lastPreamble + Str.BK;
		} else if (appConfig.traceSource.equals("low")) {
			lastPreamble = QPUtilities.getCallerNot(dontReport);
			return QPUtilities.logStamp() + Str.BK + lastPreamble + Str.BK;
		} else {
			return QPUtilities.logStamp() + Str.BK;
		}
	}

	/**
	 * Convenience method to prepare a DB2 SQL statement, passing only the connection and SQL text. Use of this code
	 * assumes the following:
	 * <ol>
	 * <li>The call is being done from the same routine that the connection was obtained from.
	 * </ol>
	 * If these assumptions are not true, the SQL call may fail, and the reported place where the error occurred will be
	 * inaccurate.
	 *
	 * @param conn - Connection to the database.
	 * @param aSql - String, the SQL text
	 * @param addOns - Zero or more additional parameters, in this order:
	 *            <p>
	 *            (1) int resultSetType <br>
	 *            (2) int resultSetConcurrency <br>
	 *            (3) int resultSetHoldability
	 *            <p>
	 *            The above have the same meaning as in the Java PreparedStatement API, and the corresponding Static
	 *            fields should be used, eg ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY.
	 *            <p>
	 *            Valid combinations are none present, (1) & (2) present, (1), (2) & 3 present.
	 * @return PreparedStatement
	 * @throws SQLException on error.
	 */
	public PreparedStatement prepareStatement(final Connection conn, final String aSql, final Object... addOns) throws SQLException {
		return prepareStatement(lastRoutine, conn, aSql, addOns);
	}

	/**
	 * Prepare a DB2 SQL statement.
	 *
	 * @param fromWhere for diagnostic purposes, from where this SQL was executed. E.g. name of the program. -
	 *            diagnostic String saying from what code this SQL statement was called.
	 * @param conn - Connection to the database.
	 * @param aSql - String, the SQL text
	 * @param addOns - Zero or more additional parameters, in this order:
	 *            <p>
	 *            (1) int resultSetType <br>
	 *            (2) int resultSetConcurrency <br>
	 *            (3) int resultSetHoldability
	 *            <p>
	 *            The above have the same meaning as in the Java PreparedStatement API, and the corresponding Static
	 *            fields should be used, eg ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY.
	 *            <p>
	 *            Valid combinations are none present, (1) & (2) present, (1), (2) & 3 present.
	 * @return PreparedStatement
	 * @throws SQLException on error.
	 */
	public PreparedStatement prepareStatement(final String fromWhere, final Connection conn, final String aSql, final Object... addOns)
	        throws SQLException {
		lastRoutine = fromWhere;
		lastSQL = aSql;
		lastSQLOp = "Prepare";

		if (appConfig.sqlDiagLevel >= AppConfig.SQL_TRACE) {
			long ts = System.currentTimeMillis();
			PreparedStatement ps = null;
			try {
				if (addOns.length < 2) {
					ps = conn.prepareStatement(aSql);
				} else if (addOns.length == 2) {
					int direction = ((Integer) addOns[0]).intValue();
					int concurr = ((Integer) addOns[1]).intValue();
					ps = conn.prepareStatement(aSql, direction, concurr);
				} else {
					int direction = ((Integer) addOns[0]).intValue();
					int concurr = ((Integer) addOns[1]).intValue();
					int hold = ((Integer) addOns[2]).intValue();
					ps = conn.prepareStatement(aSql, direction, concurr, hold);
				}
				if (trace(AppConfig.SQL_TRACE_PREPARES, ts)) {
					SQLOK(dparms, fromWhere, "Prepare", 0, ts, aSql, -1, null);
				}
				return ps;
			} catch (SQLException se) {
				SQLOK(dparms, fromWhere, deriveAccess(aSql), se.getErrorCode(), ts, aSql, -1, se);
				throw se;
			}

		}
		if (addOns.length < 2) {
			return conn.prepareStatement(aSql);
		} else if (addOns.length == 2) {
			int direction = ((Integer) addOns[0]).intValue();
			int concurr = ((Integer) addOns[1]).intValue();
			return conn.prepareStatement(aSql, direction, concurr);
		} else {
			int direction = ((Integer) addOns[0]).intValue();
			int concurr = ((Integer) addOns[1]).intValue();
			int hold = ((Integer) addOns[2]).intValue();
			return conn.prepareStatement(aSql, direction, concurr, hold);
		}
	}

	/**
	 * Really free a DB2 connection. The method {@link #freeDBConnection(Connection conn)} can sometimes be overridden.
	 *
	 * @param conn connectino to free.
	 * @throws Exception possibly on error.
	 */
	public void reallyFreeDBConnection(final Connection conn) throws Exception {
		throw new RuntimeException("Can't call getTempDBConnection in AppVars; must use application extender! " + conn);
	}

	/**
     * Recreate transient fields after deserialization<br>
     * <br>
     * In BatchJob, the AppVars instance is serialized and saved along with job
     * data in db when submitting job. Later on, when job runs, this AppVars
     * instance is deserialized with all transient fields set to null, hence
     * causes NullPointerException if those fields are not reset to approriate
     * values.
     */
    public void recreateTransientFields() {
		if (finalizableObjects == null) {
			finalizableObjects = new Hashtable();
		}
    }

	/**
	 * Update any variables as needed just before going to a new screen.
	 */
	public void reinitBeforeNewScreen() {
	}

	/**
	 * Update any variables as needed. This can be done any time, but just before a screen display, possibly in the jsp
	 * would be good.
	 */
	public void reinitVariables() {
	}

	/**
	 * implementation of ROLLBACK. The method rollbacks updates from the last execution of CMMIT or ROLLBACK; simply,
	 * call connection.rollback(); Max W
	 *
	 * @throws ExtMsgException
	 */
	public void rollback() {
		throw new UnsupportedOperationException("rollback() must be implemented in a subclass.");
	}

	/**
	 * This method is <b>NOT</b> used for <b>transaction control</b> at all. It's a convenience method to commit a
	 * connection. Application code should not rollback connection directly. This method checks if the specified
	 * connection is used for transaction control. If so, then do nothing, otherwise rollback the connection.
	 *
	 * @param conn passed Connection.
	 */
	public void rollback(final Connection conn) {
		throw new UnsupportedOperationException("rollback(Connection) must be implemented in a subclass.");
	}

	/**
	 * This method sets all the variables contained in this instance to the values contained in the passed instance. It
	 * is used for cross-ejb-layer code, where the Client side will have updated values which need to be propagated to
	 * the Server side. While this could be done with reflection, it is better to do it directly for performance
	 * reasons.
	 * <p>
	 * Extensions to this Class should also have s set method which should call this method via the super() call.
	 * <p>
	 * It is <u>essential</u> that all variables get copied. Therefore, static code has been created to check that the
	 * set method does so.
	 * <p>
	 * Exceptions should be noted, and why:
	 * <ul>
	 * <li>log - Will not be affected, logging will work fine. Also, is static.
	 * <li>store - Stores only the instance of the AppVars. So, we want it to stay pointing to the current object.
	 * <li>dontReport - static
	 * <li>APPVARS - static final
	 * <li>MESSAGE_LIMIT - static final
	 * <li>format - Doesn't matter if it's the original or a copy
	 * <li>NOACTION - static final
	 * <li>NO - static final
	 * <li>ACTION - static final
	 * <li>standaloneEnvironment - static, will not be changed by the client
	 * <li>performanceCountUpdates - static, will not be changed by the client
	 * <li>performanceDumpCountsOver - static, will not be changed by the client
	 * <li>Comparator - static
	 * <li>appName - will not change in the lifetime of an app.
	 * </ul>
	 *
	 * @param av passed instance containing the values to copy to this instance.
	 */
	public void set(final AppVars av) {

		/*
		 * If this object is pointing to the same av passed as a parametre then return
		 */
		if (this != av) {

			/*
			 * Can assign this as an object as it is private. Therefore, a local copy of AppVars cannot have this
			 * variable directly referenced, and when the data needs to be refreshed because we've just come back from a
			 * Client call on the other side of the EJB layer, reassigning the internal object can do no harm.
			 */
			messages = av.messages;

			/* Can assign it as an object as it is private */
			diagnostics = av.diagnostics;

			/* Can assign it as an object as it is private */
			timings = av.timings;

			/* Can assign it as an object as it is String */
			appName = av.appName;

			/* Can assign it as an object as it is private */
			alerts = av.alerts;

			prompt = av.prompt;

			/* Can assign it as an object as it is private */
			popup = av.popup;

			/* Can assign it as an object as it is private */
			dontReport = av.dontReport;

			lastPreamble = av.lastPreamble;
			supportPath = av.supportPath;
			nextScreen = av.nextScreen;
			nextScrXCTL = av.nextScrXCTL;

			/* Can assign it as an object as it is private */
			nextScrSm = av.nextScrSm;

			nextAction = av.nextAction;
			internalError = av.internalError;
			fieldErrorsExist = av.fieldErrorsExist;

			/* Can assign it as an object as it is private */
			appConfig = av.appConfig;

			/* Assign all field positions */
			fieldPositions.putAll(av.fieldPositions);

			transferArea = av.transferArea;
			SessionTS = av.SessionTS;
			logonRequired = av.logonRequired;
			loggedOn = av.loggedOn;
			logonNextScreen = av.logonNextScreen;
			loggedOnUser = av.loggedOnUser;
			loggedOnUserLanguage = av.loggedOnUserLanguage;
			checkedForResume = av.checkedForResume;
			lastRoutine = av.lastRoutine;
			lastSQL = av.lastSQL;
			debugAllowed = av.debugAllowed;
			lastSQLOp = av.lastSQLOp;
			screenEntry = av.screenEntry;
			heartBeat = av.heartBeat;
			popupType = av.popupType;
			collateChar = av.collateChar;
			activeField = av.activeField;
			cursorField = av.cursorField;
			sessionId = av.sessionId;
			dbConnectOK = av.dbConnectOK;
			mainFrameLoaded = av.mainFrameLoaded;

			/* Can assign it as an object as it is private */
			dparms = av.dparms;

			nextSingleModelJSPScreen = av.nextSingleModelJSPScreen;

			/* Can assign it as an object as it is private */
			ssfProgramObject = av.ssfProgramObject;

			/* Can assign it as an object as it is private */
			sqlSummary = av.sqlSummary;

			ApplicationUse0 = av.ApplicationUse0;
			ApplicationUse1 = av.ApplicationUse1;
			ApplicationUse2 = av.ApplicationUse2;
			ApplicationUse3 = av.ApplicationUse3;
			ApplicationUse4 = av.ApplicationUse4;
			ApplicationUse5 = av.ApplicationUse5;
			ApplicationUse6 = av.ApplicationUse6;
			ApplicationUse7 = av.ApplicationUse7;
			ApplicationUse8 = av.ApplicationUse8;
			ApplicationUse9 = av.ApplicationUse9;
			ApplicationUse10 = av.ApplicationUse10;
			ApplicationUse11 = av.ApplicationUse11;
			ApplicationUse12 = av.ApplicationUse12;

			/* Can assign it as it is a primitive */
			heartBeat = av.heartBeat;

			finalised = av.finalised;

			tableSchemas = av.tableSchemas;

			isTableSchemaEnabled = av.isTableSchemaEnabled;
		}

		/*
		 * If we come in again on the same Thread, and this Thread's instance of AppVars.getInstance() in the
		 * ThreadLocal has been zapped, the transient fields need to be recreated.
		 */
//		if (finalizableObjects == null) {
//			finalizableObjects = new Hashtable();
//	    }
		recreateTransientFields();
		if (dontReportStatic == null) {
			dontReportStatic = dontReport;
		}

		/* The static fields, and those in AppConfig may need to be copied too. */

	}

	/**
	 * Setter for {@link #activeField}.
	 *
	 * @param pActiveField new valure for {@link #activeField}.
	 */
	public void setActiveField(final String pActiveField) {
		activeField = pActiveField;
	}

	 /**
	 * Setter for {@link #appConfig}.
	 *
	 * @param pAppConfig new value for {@link #appConfig}
	 */
	protected void setAppConfig(final AppConfig pAppConfig) {
		this.appConfig = pAppConfig;
	}

	/**
	 * Available to set the Application Name ({@link #appName}). This is obtained by the Servlet that handles the
	 * application, which should obtain it as a parameter from the Web Container.
	 *
	 * @param pAppName new vlaue for {@link #appName}.
	 */
	public void setApplicationName(final String pAppName) {
		appName = pAppName;
	}

	// add the setBusinessdate for modifying the current businessdate  (xma3 2009-08-21)
	public void setBusinessdate(String businessdate) {
		this.businessdate = businessdate;
	}

	public void setBusinessDateFlag(boolean businessDateFlag) {
		this.businessDateFlag = businessDateFlag;
	}

	/**
	 * Setter for {@link #checkedForResume}.
	 *
	 * @param pCheckedForResume The new value for {@link #checkedForResume}.
	 */
	public void setCheckedForResume(final int pCheckedForResume) {
		this.checkedForResume = pCheckedForResume;
	}

	/**
     * Setter of collateChar, which can be overridden by project specific
     * AppVars
     *
     * @param collateChar - String containing single character representing
     *            HiValues.
     */
    protected void setCollateChar(final String collateChar) {
        this.collateChar = collateChar;
    }

	public void setCompanyBranchArray(String[][] companyBranchArray) {
		this.companyBranchArray = companyBranchArray;
	}

	/**
	 * Setter for {@link #cursorField}.
	 *
	 * @param pCursorField new value for {@link #cursorField}.
	 */
	public void setCursorField(final String pCursorField) {
		cursorField = pCursorField;
	}

	/**
	 * Sets a prepared statement column to the passed BigDecimal and logs the value in the internal array of parameters
	 * which is used for tracing and debugging purposes.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value value for the parameter.
	 * @throws SQLException on error.
	 */
	public void setDBBigDecimal(final PreparedStatement ps, final int parmno, final BigDecimal value) throws SQLException {
		ps.setBigDecimal(parmno, value);
		if (parmno < dparms.length) {
			dparms[parmno] = value;
		}
	}

	/**
	 * Sets a prepared statement column to the passed input stream value and logs the value in the internal array of
	 * parameters which is used for tracing and debugging purposes.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed input stream.
	 * @param len length to set.
	 * @throws SQLException on error.
	 */
	public void setDBBinaryStream(final PreparedStatement ps, final int parmno, final ByteArrayInputStream value, final int len)
	        throws SQLException {
		ps.setBinaryStream(parmno, value, len);
		if (parmno < dparms.length) {
			dparms[parmno] = "Binary Stream, len=";
		}
	}

	/**
	 * Sets a database date parameter to the passed value and logs the value in the internal array of parameters which
	 * is used for tracing and debugging purposes. All reasonable attempts are made to convert the passed Object to a
	 * Date, or extract its Date value, if it is not already a Date. It cannot be null.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed Object.
	 * @throws SQLException on error.
	 */
	public void setDBDate(final PreparedStatement ps, final int parmno, final Object value) throws SQLException {
		if (value == null) {
			throw new RuntimeException("Null date not acceptable.");
		}
		if (value instanceof Date) {
			ps.setDate(parmno, (Date) value);
			if (parmno < dparms.length) {
				dparms[parmno] = value.toString();
			}
		} else if (value instanceof java.util.Date) {
			java.util.Date d = (java.util.Date) value;
			long l = d.getTime();
			ps.setDate(parmno, new Date(l));
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		} else if (value instanceof DateData) {
			ps.setDate(parmno, ((DateData) value).getData());
			if (parmno < dparms.length) {
				dparms[parmno] = ((DateData) value).getData().toString();
			}
		} else if (value instanceof FastNIBase) {
			Date d = ((FastNIBase) value).toEncodedDate();
			ps.setDate(parmno, d);
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		} else {
			Date d = Date.valueOf(value.toString());
			ps.setDate(parmno, d);
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		}
	}

	/**
	 * Sets a database double parameter to the passed value and logs the value in the internal array of parameters which
	 * is used for tracing and debugging purposes.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBDouble(final PreparedStatement ps, final int parmno, final double value) throws SQLException {
		ps.setDouble(parmno, value);
		if (parmno < dparms.length) {
			dparms[parmno] = new Double(value);
		}
	}

	/**
	 * Sets a database int parameter to the passed value and logs the value in the internal array of parameters which is
	 * used for tracing and debugging purposes.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBInt(final PreparedStatement ps, final int parmno, final int value) throws SQLException {
		ps.setInt(parmno, value);
		if (parmno < dparms.length) {
			dparms[parmno] = Integer.valueOf(value);
		}
	}

	/**
	 * Sets a database long parameter to the passed value and logs the value in the internal array of parameters which
	 * is used for tracing and debugging purposes.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBLong(final PreparedStatement ps, final int parmno, final long value) throws SQLException {
		ps.setLong(parmno, value);
		if (parmno < dparms.length) {
			dparms[parmno] = Long.valueOf(value);
		}
	}

	/**
	 * Sets a database parameter to the passed value and logs the value in the internal array of parameters which is
	 * used for tracing and debugging purposes. This method looks at the passed variable, and if it has a precision
	 * greater than 15 uses getBigDecimal, otherwise it delegates the work to setDBLong, setDBInt or setDBDouble.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBNumber(final PreparedStatement ps, final int parmno, final FastNIBase value) throws SQLException {
		if (value.getPrecision() > Num.I15) {
			BigDecimal bd = value.getbigdata();
			ps.setBigDecimal(parmno, bd);
			if (parmno < dparms.length) {
				dparms[parmno] = bd;
			}
		} else if (value.getScaleOrZero() == 0 && value.getPrecision() > Num.I10) {
			setDBLong(ps, parmno, value.toLong());
		} else if (value.getScaleOrZero() == 0) {
			setDBInt(ps, parmno, value.toInt());
		} else {
			setDBDouble(ps, parmno, value.toDouble());
		}
	}

	/**
	 * Set up DB parameters for a PrepareStatement
	 *
	 * @param parms
	 * @throws SQLException
	 */
	public void setDBParameters(final PreparedStatement aPs, final Object[] parms) throws SQLException {
		setDBParameters(aPs, parms, 0);

	}

	/**
	 * Set up DB parameters for a PrepareStatement
	 *
	 * @param parms
	 * @throws SQLException
	 */
	public int setDBParameters(final PreparedStatement aPs, final Object[] parms, int startIndex) throws SQLException {
		Object o = null;
		int i =0;
		for (int index = 0; parms != null && index < parms.length; index++) {

			o = parms[index];
			i = startIndex;

			if (o == null) {
				setDBString(aPs, i + 1, null);
			} else if (o instanceof StringBase || o instanceof String) {
				setDBString(aPs, i + 1, o);
			} else if (o instanceof FastNIBase) {
				setDBNumber(aPs, i + 1, (FastNIBase) o);
			} else if (o instanceof TimestampData) {
				setDBTimestamp(aPs, i + 1, o);
			} else if (o instanceof RPGTimestampData) {
				setDBTimestamp(aPs, i + 1, o);
			} else if (o instanceof DateData) {
				setDBDate(aPs, i + 1, o);
			} else if (o instanceof RPGDateData) {
				setDBDate(aPs, i + 1, o);
			} else if (o instanceof TimeData) {
				setDBTime(aPs, i + 1, o);
			} else if (o instanceof RPGTimeData) {
				setDBTime(aPs, i + 1, o);
			} else if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
				setDBInt(aPs, i + 1, ((Number) o).intValue());
			} else if (o instanceof Double || o instanceof Float) {
				setDBDouble(aPs, i + 1, ((Number) o).doubleValue());
			} else if (o instanceof Timestamp) {
				setDBTimestamp(aPs, i + 1, o);
			} else if (o instanceof Date) {
				setDBDate(aPs, i + 1, o);
			} else if (o instanceof TimeData) {
				setDBTime(aPs, i + 1, o);
			} else if (o instanceof BigDecimal) {
				setDBBigDecimal(aPs, i + 1, (BigDecimal) o);
			} else {
				throw new RuntimeException(TYPE + o.getClass().getName() + CINDEX + i + NOT_CATERED);
			}

			startIndex = startIndex+1;
		}

		return startIndex;

	}

	/**
	 * See {@link #setDBString(PreparedStatement, int, Object, int)}. Equivalent to that method with the 4th parameter
	 * zero.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBString(final PreparedStatement ps, final int parmno, final Object value) throws SQLException {
		setDBString(ps, parmno, value, 0);
	}

	/**
	 * Sets a database String parameter to the passed value and logs the value in the internal array of parameters which
	 * is used for tracing and debugging purposes. See parameter "len" for information about trucation/padding.
	 * <p>
	 * If {@link AppConfig#DBCSLanguage} is not null, and {@link AppConfig#dbcsForDB} is on, the value will be assumed
	 * to be in internal DBCS format and requiring conversion to DBCS prior to use. See the above references for a more
	 * detailed description.
	 *
	 * @param ps - PreparedStatement
	 * @param parmno - normal meaning in PreparedStatement.setString
	 * @param value - value, can be null
	 * @param len - length of the value expected by the database. Processing is:
	 *            <ol>
	 *            <li>If {@link AppConfig#truncateSQLParms} is on, ignored, and trailing spaces will be removed. This is
	 *            typically appropriate for DB2. <li>If {@link AppConfig#truncateSQLParms} is off, and len greater than
	 *            zero, the String will be padded out on the right (or truncated) to the length specified. <li>
	 *            Otherwise, the length of the passed value is not changed.
	 *            </ol>
	 * @throws SQLException on error.
	 */
	public void setDBString(final PreparedStatement ps, final int parmno, final Object value, final int len) throws SQLException {
		Object cvalue = value;
		if (cvalue == null) {
			ps.setString(parmno, null);
			if (parmno < dparms.length) {
				dparms[parmno] = null;
			}
			return;
		}
		//UTF-8 Related Change
		if (cvalue instanceof StringBase)
		{
			cvalue = ((StringBase) cvalue).toString();
		} else if (cvalue instanceof StringBuilder || cvalue instanceof StringBuffer)
		{
			cvalue = cvalue.toString();
			}
			/* Else - leave it alone ! */

		if (getAppConfig().truncateSQLParms) {
			ps.setString(parmno, QPUtilities.trimRight(cvalue.toString()));
		} else if (len > 0) {
			ps.setString(parmno, QPUtilities.padRightAndAdjustSupplementaryChars(cvalue.toString(), len));
		} else {
			ps.setString(parmno, QPUtilities.padRightAndAdjustSupplementaryChars(cvalue.toString(),
			cvalue.toString().length()));
		}
		if (parmno < dparms.length) {
			dparms[parmno] = cvalue;
		}
	}

	/**
	 * Sets a database time parameter to the passed value and logs the value in the internal array of parameters which
	 * is used for tracing and debugging purposes. All reasonable attempts are made to convert the passed Object to a
	 * Time, or extract its Time value, if it is not already a Time. It cannot be null.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBTime(final PreparedStatement ps, final int parmno, final Object value) throws SQLException {
		if (value == null) {
			throw new RuntimeException("Null date not acceptable.");
		}
		if (value instanceof Time) {
			ps.setTime(parmno, (Time) value);
			if (parmno < dparms.length) {
				dparms[parmno] = value.toString();
			}
		} else if (value instanceof TimeData) {
			ps.setString(parmno, ((TimeData) value).getData());
			if (parmno < dparms.length) {
				dparms[parmno] = ((TimeData) value).getData();
			}
		} else if (value instanceof String) {
			ps.setString(parmno, (String) value);
			if (parmno < dparms.length) {
				dparms[parmno] = (String) value;
			}
		} else if (value instanceof RPGTimeData) {
			Time d = ((RPGTimeData) value).toTime();
			ps.setTime(parmno, d);
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		} else {
			Time d = Time.valueOf(value.toString());
			ps.setTime(parmno, d);
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		}
	}

	/**
	 * Sets a database timestamp parameter to the passed value and logs the value in the internal array of parameters
	 * which is used for tracing and debugging purposes. All reasonable attempts are made to convert the passed Object
	 * to a Timestamp, or extract its Timestamp value, if it is not already a Timestamp. It cannot be null.
	 *
	 * @param ps the passed prepared statement.
	 * @param parmno column number within the prepared statement to use.
	 * @param value the passed value.
	 * @throws SQLException on error.
	 */
	public void setDBTimestamp(final PreparedStatement ps, final int parmno, final Object value) throws SQLException {
		if (value == null) {
			throw new RuntimeException("Null date not acceptable.");
		}
		if (value instanceof Timestamp) {
			ps.setTimestamp(parmno, (Timestamp) value);
			if (parmno < dparms.length) {
				dparms[parmno] = value.toString();
			}
		} else if (value instanceof TimestampData) {
			ps.setTimestamp(parmno, ((TimestampData) value).getData());
			if (parmno < dparms.length) {
				dparms[parmno] = value.toString();
			}
		} else if (value instanceof RPGTimestampData) {
			ps.setTimestamp(parmno, ((RPGTimestampData) value).toTimestamp());
			if (parmno < dparms.length) {
				dparms[parmno] = value.toString();
			}
		} else {
			Timestamp d = Timestamp.valueOf(value.toString());
			ps.setTimestamp(parmno, d);
			if (parmno < dparms.length) {
				dparms[parmno] = d.toString();
			}
		}
	}

	/**
	 * Setter for {@link #debugAllowed}.
	 *
	 * @param pDebugAllowed new value for {@link #debugAllowed}.
	 */
	public void setDebugAllowed(final int pDebugAllowed) {
		this.debugAllowed = pDebugAllowed;
	}

	/**
	 * Gets a named field in this Class and sets its value via reflection.
	 *
	 * @param f name of field to set.
	 * @param o new value for the field.
	 * @throws Exception on error.
	 */
	public void setDebuggingField(final String f, final Object o) throws Exception {
		this.getClass().getDeclaredField(f).set(this, o);
	}

	/**
	 * Setter for {@link #diagnostics}.
	 *
	 * @param pDiagnostics new value for {@link #diagnostics}.
	 */
	public void setDiagnostics(final MessageList pDiagnostics) {
		diagnostics = pDiagnostics;
	}

	/**
	 * Setter for {@link #dontReport}.
	 *
	 * @param pDontReport new value for {@link #dontReport} and {@value #dontReportStatic}.
	 */
	public void setDontReport(final String pDontReport) {
		dontReport = pDontReport;
		dontReportStatic = pDontReport;
	}

	/**
	 * Setter for {@link #heartBeat}.
	 *
	 * @param pHeartBeat new value for {@link #heartBeat}.
	 */
	public void setHeartBeat(final long pHeartBeat) {
		heartBeat = pHeartBeat;
	}

	public void setLanguageArray(String[] languageArray) {
		this.languageArray = languageArray;
	}
	/**
	 * @param languageCode the languageCode to set.
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * Setter for {@link #loggedOn}.
	 *
	 * @param pLoggedOn new value for {@link #loggedOn}.
	 */
	public void setLoggedOn(final boolean pLoggedOn) {
		this.loggedOn = pLoggedOn;
	}

	/**
	 * Setter for {@link #loggedOnUser}.
	 *
	 * @param pLoggedOnUser new value for {@link #loggedOnUser}.
	 */
	public void setLoggedOnUser(final String pLoggedOnUser) {
		this.loggedOnUser = pLoggedOnUser;
	}

	/**
	 * Setter for {@link #logonNextScreen}.
	 *
	 * @param pLogonNextScreen new value for {@link #logonNextScreen}.
	 */
	public void setLogonNextScreen(final String pLogonNextScreen) {
		this.logonNextScreen = pLogonNextScreen;
	}

	/**
	 * Enhanced setter for {@link #nextSingleModelJSPScreen}. A leading "/" and a trailing ".jsp" will be removed.
	 *
	 * @param pNextScreen new value for {@link #nextSingleModelJSPScreen}.
	 */
	public void setNextSingleModelJSPScreen(String pNextScreen) {
		pNextScreen = QPUtilities.removeLeading(pNextScreen, "/");
		if (isAppendingSuffix()) {
			pNextScreen = QPUtilities.removeTrailing(pNextScreen, ".jsp");
			nextSingleModelJSPScreen = pNextScreen + "Form.jsp";
		} else {
			nextSingleModelJSPScreen = pNextScreen;
		}
	}

	public void SetMenuDisplayed(boolean b) {
        this.isMenuDisplayed = b;

        /**
         * Fix bug 3433 (large memory consumption)
         *
         * The problem was that the finalizableObjects quickly grew bigger and
         * bigger as the user went on web GUI. There was no mechanism to release
         * data in this field, so after a while, it expands to the extent that
         * almost all memory is taken up.
         *
         * The solution here is to clear up finalizableObjects as soon as user
         * finishes the workflow (completes the last screen in the screen flow).
         * I notice that any screens in the middle of the workflow don't have
         * the main menu displayed on the left side, so we can base on the
         * presence of main menu to know if user has finished the workflow or
         * not, and clear the finalizableObjects accordingly. The main menu is
         * only displayed when there is a call to AppVars.SetMenuDisplayed(true)
         */
        if (b) {
            LOGGER.debug("[AppVars] Main Menu is set to be displayed --> clear finalizableObjects");
            this.finalizableObjects.clear();
        }
    }

	/**
	 * Setter for {@link #popup}'s menu. See {@link PopupArrayList#setMenu(boolean)}.
	 *
	 * @param val value passed on.
	 */
	public void setPopupMenu(final boolean val) {
		popup.setMenu(val);
	}

	/**
	 * Setter for {@link #popupType}.
	 *
	 * @param pPopupType new value for {@link #popupType}.
	 */
	public void setPopupType(final String pPopupType) {
		popupType = pPopupType;
	}

	/**
	 * Setter for {@link #popup}'s values. See also {@link PopupArrayList#setValues(int)}.
	 *
	 * @param no value passed on.
	 */
	public void setPopupValues(final int no) {
		popup.setValues(no);
	}

	private void setPreparedStatementParam(PreparedStatement ps, Object o, int i) throws SQLException {
    	if (o == null || o instanceof StringBase || o instanceof String) {
			setDBString(ps, i + 1, o);
		} else if (o instanceof FastNIBase) {
			setDBNumber(ps, i + 1, (FastNIBase) o);
		} else if (o instanceof TimestampData) {
			setDBTimestamp(ps, i + 1, o);
		} else if (o instanceof RPGTimestampData) {
			setDBTimestamp(ps, i + 1, o);
		} else if (o instanceof DateData) {
			setDBDate(ps, i + 1, o);
		} else if (o instanceof RPGDateData) {
			setDBDate(ps, i + 1, o);
		} else if (o instanceof TimeData) {
			setDBTime(ps, i + 1, o);
		} else if (o instanceof RPGTimeData) {
			setDBTime(ps, i + 1, o);
		} else if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
			setDBInt(ps, i + 1, ((Number) o).intValue());
		} else if (o instanceof Long) {
			setDBLong(ps, i + 1, ((Long) o).longValue());
		} else if (o instanceof Double || o instanceof Short) {
			setDBDouble(ps, i + 1, ((Long) o).longValue());
		} else if (o instanceof Timestamp) {
			setDBTimestamp(ps, i + 1, o);
		} else if (o instanceof Date) {
			setDBDate(ps, i + 1, o);
		} else if (o instanceof TimeData) {
			setDBTime(ps, i + 1, o);
		} else if (o instanceof BigDecimal) {
			setDBBigDecimal(ps, i + 1, ((BigDecimal) o));
		} else {
			throw new RuntimeException(TYPE + o.getClass().getName() + CINDEX + i + NOT_CATERED);
		}
    }

	/**
	 *
	 * @param ps
	 * @param parmno
	 * @param value
	 * @throws SQLException
	 */
	public void setRaw(PreparedStatement ps, int parmno, Object value) throws SQLException {
		Object cvalue = value;
		if (cvalue == null) {
			ps.setString(parmno, null);
			if (parmno < dparms.length) {
				dparms[parmno] = null;
			}
			return;
		}

		try {
			ps.setBytes(parmno, cvalue.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			new RuntimeException(e);
		}
		if (parmno < dparms.length) {
			dparms[parmno] = cvalue;
		}
	}

	/**
	 * @param resourceBundle the resourceBundle to set
	 */
	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	/**
	 * Setter for {@link #screenEntry}.
	 *
	 * @param pScreenEntry new value for {@link #screenEntry}.
	 */
	public void setScreenEntry(final long pScreenEntry) {
		this.screenEntry = pScreenEntry;
	}

	/**
	 * Setter for {@link #sessionId}.
	 *
	 * @param pSessionId new value for {@link #sessionId}.
	 */
	public void setSessionId(final String pSessionId) {
		this.sessionId = pSessionId;
	}

	/**
	 * Setter for {@link #ssfProgramObject}.
	 *
	 * @param pSsfProgramObject new value for {@link #ssfProgramObject}.
	 */
	public void setSsfProgramObject(final CodeModel pSsfProgramObject) {
		ssfProgramObject = pSsfProgramObject;
	}

	/**
	 * Setter for {@link #supportPath}.
	 *
	 * @param pSupportPath new value for {@link #supportPath}.
	 */
	public void setSupportPath(final String pSupportPath) {
		this.supportPath = pSupportPath;
	}

	/**
	 * Setter for {@link #transferArea}.
	 *
	 * @param pTransferArea new value for {@link #transferArea}.
	 */
	public void setTransferArea(final String pTransferArea) {
		this.transferArea = pTransferArea;
	}

	/**
	 * Enhanced setter for {@link #loggedOnUserLanguage}. If the passed new value is not null, if will be trimmed.
	 *
	 * @param pLoggedOnUserLanguage new value for {@link #loggedOnUserLanguage}.
	 */
	public void setUserLanguage(final Object pLoggedOnUserLanguage) {
		String strLang = (pLoggedOnUserLanguage == null) ? null : pLoggedOnUserLanguage.toString().trim();
		loggedOnUserLanguage.set(strLang);
	}

	/**
	 * Method showScreen. See {@link #showScreen(String, ScreenModel, String)}. Same functionality, except no subsequent
	 * action, and a default, empty ScreenModel will be constructed.
	 *
	 * @param pScreen see delegate.
	 */
	public void showScreen(final String pScreen) {
		showScreen(pScreen, new ScreenModel(pScreen, this), ACTION);
	}

	/**
	 * Method showScreen. See {@link #showScreen(String, ScreenModel, String)}. Same functionality, except no subsequent
	 * action.
	 *
	 * @param pScreen see delegate.
	 * @param pSm see delegate.
	 * @throws WebServerException see delegate.
	 */
	public void showScreen(final String pScreen, final ScreenModel pSm) throws WebServerException {
		showScreen(pScreen, pSm, ACTION);
	}

	/**
	 * Method showScreen. Shows a new screen, but on exiting that one, return to this one.
	 *
	 * @param pScreen passed screen name.
	 * @param pSm initialised screenmodel for that screen, usually present because some screen data has been
	 *            initialised. Otherwise, there is a version of this method which will create a new, appropriate and
	 *            empty screen model.
	 * @param pNextAction a String containing what action will be executed when the new screen ends, and this one is
	 *            resumed. If required, this is typically a method to refresh data, get messages etc.
	 */
	public void showScreen(String pScreen, final ScreenModel pSm, final String pNextAction) {
		if (pScreen == null) {
			pScreen = "";
		}
		pScreen = pScreen.trim();
		if (QPUtilities.isEmpty(pScreen)) {
			throw new RuntimeException("Attempt to switch to screen '" + pScreen
			        + "', but a previous request to switch to screen '" + nextScreen + "' is outstanding.");
		}
		nextScreen = pScreen;
		nextScrXCTL = false;
		nextScrSm = pSm;
		nextAction = NO + pNextAction;
	}

	/**
	 * Delegates to {@link #SQLError(String, String, SQLException, String)}, calling {@link #deriveAccess(String)} on
	 * the SQL parameter.
	 *
	 * @param progName see delegate.
	 * @param ex1 see delegate.
	 * @param aSql see delegate.
	 */
	public void SQLError(final String progName, final SQLException ex1, final String aSql) {
		SQLError(progName, deriveAccess(aSql), ex1, aSql);
	}

	/**
	 * Method SQLError. Not intended for application use. This version formats an SQL statement that did not work, and
	 * includes its timimgs.
	 *
	 * @param progName name of the program issuing the SQL as far as reporting is concerned.
	 * @param access type of SQL access, e.g. SELECT.
	 * @param ex1 exception trapped.
	 * @param startTs start time of executing the SQL.
	 * @param aSql SQL that failed.
	 */
	public void SQLError(final String progName, final String access, final SQLException ex1, final long startTs, String aSql) {
		int p = 0;
		for (int i = 1; i < Num.I1K; i++) {
			p = aSql.indexOf('?');
			if (p < 0) {
				break;
			}
			if (dparms[i] == null) {
				break;
			}
			String s = null;
			if (dparms[i] instanceof StringBase) {
				s = Str.APOST + ((BaseData) dparms[i]).toString() + Str.APOST;
			} else {
				s = ((BaseData) dparms[i]).toString();
			}
			aSql = aSql.substring(0, p) + s + aSql.substring(p + 1);
		}
		String msg = RED + progName + " SQLCode " + QPUtilities.formatI(ex1.getErrorCode(), Num.I4) + Str.BK + access
		        + Str.BK + lastSQLOp + Str.BK + QPUtilities.sSecs(startTs) + " </font>"
		        + QPUtilities.replaceSubstring(aSql, Str.APOST, HTML_39) + "<br>"
		        + QPUtilities.replaceSubstring(ex1.toString(), Str.APOST, HTML_39);

		boolean traceoff = appConfig.traceSource.equals("off");
		if (traceoff) {
			appConfig.traceSource = "on";
		}

		addDiagnostic(msg, AppConfig.ERROR);
		addMessage(msg);

		if (traceoff) {
			appConfig.traceSource = "off";
		}
	}

	/**
	 * Method SQLError. Not intended for application use. This version formats an SQL statement that did not work, and
	 * does not include timimgs.
	 *
	 * @param progName name of the program issuing the SQL as far as reporting is concerned.
	 * @param access type of SQL access, e.g. SELECT.
	 * @param ex1 exception trapped.
	 * @param aSql SQL that failed.
	 */
	public void SQLError(final String progName, final String access, final SQLException ex1, String aSql) {
		int p = 0;
		if (aSql == null) {
			aSql = "No SQL passed to error routine!";
		}
		for (int i = 1; i < 1000; i++) {
			p = aSql.indexOf('?');
			if (p < 0) {
				break;
			}
			if (dparms[i] == null) {
				break;
			}
			String s = null;
			if (dparms[i] == null) {
				s = "null";
			} else if (dparms[i] instanceof BaseData) {
				s = ((BaseData) dparms[i]).toString();
				if (s == null) {
					s = "null";
				}
				s = QPUtilities.trimRight(s);
				if (dparms[i] instanceof StringBase) {
					s = Str.APOST + s + Str.APOST;
				}
			} else {
				s = dparms[i].toString();
			}
			aSql = aSql.substring(0, p) + s + aSql.substring(p + 1);
		}
		String msg = RED + progName + " SQLCode " + QPUtilities.formatI(ex1.getErrorCode(), Num.I4) + Str.BK + access
		        + Str.BK + lastSQLOp + " -.-- </font>" + QPUtilities.replaceSubstring(aSql, Str.APOST, HTML_39)
		        + "<br>" + QPUtilities.replaceSubstring(ex1.toString(), Str.APOST, HTML_39);

		String trace = appConfig.traceSource;
		try {
			appConfig.traceSource = "on";
			addDiagnostic(msg, AppConfig.ERROR);
			addMessageNoDebug(new BaseMessage(msg));
		} catch (Exception e) {
			// Should be impossible, but maybe AppVars is cleaned up.
			// In that case, nowhere to report the error so ignore it.
			doNothing();
		}

		appConfig.traceSource = trace;

		throw new RuntimeException(ex1.toString(), ex1);
	}

	/**
	 * Delegates to {@link #SQLError(String, SQLException, String)} but traps any exception and ignores it.
	 *
	 * @param fromWhere see delegate.
	 * @param ex1 see delegate.
	 * @param aSql see delegate.
	 */
	public void SQLErrorNoAbend(final String fromWhere, final SQLException ex1, final String aSql) {
		try {
			SQLError(fromWhere, deriveAccess(aSql), ex1, aSql);
		} catch (Exception e) {
			// As we're ignoring any error, ignore it.
			doNothing();
		}
	}

	/**
	 * Method SQLOK. Not intended for application use.
	 *
	 * @param pDparms passed array of paramters logged as having been used in the passed SQL call.
	 * @param progName for diagnostic purposes, from where this SQL was executed. E.g. name of the program. - diagnostic
	 *            String saying from what code this SQL statement was called.
	 * @param access - diagnostic String identifying the SQL call type.
	 * @param SQLCode - diagnostic String identifying the SQL result.
	 * @param startTs - long, when the call started.
	 * @param aSql - String, the SQL text.
	 * @param rows - Number of rows affected.
	 * @param se - Exception resulting, can be null.
	 */
	public void SQLOK(final Object[] pDparms, final String progName, final String access, final int SQLCode, final long startTs, final String aSql,
	        final int rows, final SQLException se) {

		if ((SQLCode < 0 && SQLCode != -803) || QPUtilities.mSecs(startTs) >= appConfig.sqlEDiagLevel) {
			String sqlErr = "    (No SQL Error Message available)";
			if (se != null) {
				sqlErr = "    " + se.getLocalizedMessage();
				sqlErr = QPUtilities.removeTrailing(sqlErr, Str.SYS_NL);
				sqlErr = QPUtilities.removeTrailing(sqlErr, RET);
				addError(formatDiagnosticLongSQL(pDparms, aSql + Str.SYS_NL + sqlErr, access, progName, SQLCode, startTs));
			} else if (QPUtilities.mSecs(startTs) >= appConfig.sqlEDiagLevel) {
				// SQL Call took too long add a message to the Info Log
				String sqlInfo = "    SQL Call took longer than " + appConfig.sqlEDiagLevel + " msec";
				addDiagnostic((formatDiagnosticLongSQL(pDparms, aSql + Str.SYS_NL + sqlInfo, access, progName, SQLCode, startTs)), AppConfig.INFO);
			} else {
				addError(formatDiagnosticLongSQL(pDparms, aSql + Str.SYS_NL + sqlErr, access, progName, SQLCode, startTs));
			}

			if (SQLCode < 0 && SQLCode != -803) {
				// addError(SQLErr);
				String msg = RED + progName + " SQLCode " + QPUtilities.formatI(SQLCode, Num.I4) + Str.BK + access
				        + Str.BK + lastSQLOp + " -.-- </font>" + QPUtilities.replaceSubstring(aSql, Str.APOST, HTML_39)
				        + "<br>" + QPUtilities.replaceSubstring(sqlErr, Str.APOST, HTML_39);

				addMessageNoDebug(new BaseMessage(msg));
			}
		} else {
			if (!appConfig.sqlSummaryOnly.equalsIgnoreCase("yes")) {
				if (appConfig.sqlDiagLevel > AppConfig.SQL_REPORT_FETCHES || !access.trim().toUpperCase().equals("FETCH")) {
					addDiagnostic(formatDiagnosticSQL(pDparms, aSql, access, progName, SQLCode, startTs, rows));
				}
			}
		}

		String diagKey = postamble().substring(2);
		if (sqlSummary == null) {
			sqlSummary = new Hashtable();
		}
		BaseData[] diagData = (BaseData[]) (sqlSummary.get(diagKey));
		if (diagData != null) {
			diagData[1].add(QPUtilities.mSecs(startTs) / Num.I1K);
			diagData[2].plusplus();

		} else {
			diagData = new BaseData[] { new FixedLengthStringData(access),
			        new DecimalData(Num.I9, Num.I3, QPUtilities.mSecs(startTs) / Num.I1K, COMMA_SEP),
			        new IntegerData(1) };
		}
		sqlSummary.put(diagKey, diagData);

	}

	/**
	 * Placeholder for a mathod to start commitment control. By this we mean that from now on, SQL calls will occur but
	 * will eventually need to be committed or rolled back. The implication is that before this, SQL statments were
	 * committed as they occurred. See also {@link Connection#setAutoCommit(boolean)} for a similar sort of thing.
	 * <p>
	 * This implementation throws an exception as what is to be done is database dependent, and so must be implemented
	 * in a subclass.
	 */
	public void startCommitControl() {
		throw new UnsupportedOperationException("startCommitControl() must be implemented in subclass.");
	}

	/**
	 * Cut string support Supplementary character. This function will cut string
	 * base on real character count.
	 *
	 * @param str
	 * @param length
	 *            number of real character to cut
	 * @return substring has number of real character equal param was given
	 */
	private String subSupplementaryChars(String str, int length) {
		char[] charArray = str.toCharArray();
		int index = 0;
		int countChar = 0;
		while (index < charArray.length) {
			int codePoint = Character.codePointAt(charArray, index);
			index = index + Character.charCount(codePoint);
			countChar++;
			if (countChar == length) {
				break;
			}
		}

		return str.substring(0, index);
	}

	/**
	 * Method switchScreen. See {@link #switchScreen(String, ScreenModel)}.
	 *
	 * @param pScreen name of screen to switch to.
	 */
	public void switchScreen(String pScreen) {
		if (pScreen == null) {
			pScreen = "";
		}
		pScreen = pScreen.trim();
		ScreenModel lsm = new ScreenModel(pScreen, this);
		if (lsm.getXMLScreen().getScreenInit() == null) {
			lsm = new ScreenModel("UNKNOWN", this);
			lsm.getField("Target").set(pScreen);
		}
		switchScreen(pScreen, lsm);
	}

	/**
	 * Method switchScreen. When called from an application, indicates that when this screen finishes its currently
	 * executing methods, it should be discarded and the new screen shown instead. The stack is preserved, i.e. pressing
	 * "Previous Screen" on this new screen will return to this one's parent.
	 *
	 * @param pScreen - screen to show.
	 * @param pSm - Initialised screenmodel for that screen, usually present because some screen data has been
	 *            initialised. Otherwise, there is a version of this method which will create a new, appropriate and
	 *            empty screen model.
	 */
	public void switchScreen(final String pScreen, final ScreenModel pSm) {
		if (QPUtilities.isEmpty(pScreen)) {
			throw new RuntimeException("Attempt to switch to screen '" + pScreen
			        + "', but a previous request to switch to screen '" + nextScreen + "' is outstanding.");
		}
		nextScreen = pScreen;
		nextScrXCTL = true;
		nextScrSm = pSm;
		nextAction = NOACTION;
	}

	/**
	 * Delegates to {@link QPUtilities#dumpClass(Object, Object...)} to generate a diagnostic String dump.
	 *
	 * @return as per description.
	 */
	@Override
	public String toString() {
		return QPUtilities.dumpClass(this);
	}

	/**
	 * Checks if
	 * <ol>
	 * <li>{@link AppConfig#sqlDiagLevel} &gt;= the passed diagnostic level
	 * <li>the difference between now and the passed start time of an SQL call, in milliseconds, &gt;=
	 * {@link AppConfig#sqlEDiagLevel}.
	 * </ol>
	 *
	 * @param typeOfCall the passed diagnostic level.
	 * @param startTime the passed start time of an SQL call.
	 * @return true if either condition is true.
	 */
	private boolean trace(final int typeOfCall, final long startTime) {
		return (appConfig.sqlDiagLevel >= typeOfCall) || (QPUtilities.mSecs(startTime) >= appConfig.sqlEDiagLevel);
	}

	/**
	 * Method update. Not for application use. Updates an AppVars that has come back from the Server with the values
	 * contained therein.
	 *
	 * @param newav new instance containing values to update.
	 */
	public void update(final AppVars newav) {
		this.alerts = newav.alerts;
		this.diagnostics = newav.diagnostics;
		this.fieldErrorsExist = newav.fieldErrorsExist;
		this.internalError = newav.internalError;
		this.messages = newav.messages;
		this.nextAction = newav.nextAction;
		this.nextScreen = newav.nextScreen;
		this.nextScrSm = newav.nextScrSm;
		this.nextScrXCTL = newav.nextScrXCTL;
		this.popup = newav.popup;
		this.prompt = newav.prompt;
		this.timings = newav.timings;
		this.transferArea = newav.transferArea;
	}

	/**
	 * Sets a ResultSet column to the passed BigDecimal and logs the value in a StringBuffer which is used for tracing
	 * and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @param len ignored.
	 * @throws SQLException on error.
	 */
	public void updateDBBigDecimal(final ResultSet rs, final int parmno, final BigDecimal value, final int len) throws SQLException {
		rs.updateBigDecimal(parmno, value);
		logParm(rs, parmno);
	}

	/**
	 * Sets a ResultSet column to the passed Binary Stream and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @param len length of the stream
	 * @throws SQLException on error.
	 */
	public void updateDBBinaryStream(final ResultSet rs, final int parmno, final ByteArrayInputStream value, final int len) throws SQLException {
		rs.updateBinaryStream(parmno, value, len);
		logParm(rs, parmno);
	}

	/**
	 * Sets a ResultSet column to the passed date parameter and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent. All
	 * reasonable attempts are made to convert the passed Object to a Date, or extract its Date value, if it is not
	 * already a Date. It cannot be null.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBDate(final ResultSet rs, final int parmno, final Object value) throws SQLException {
		if (value == null) {
			throw new RuntimeException("Null date not acceptable.");
		}
		if (value instanceof Date) {
			rs.updateDate(parmno, (Date) value);
		} else if (value instanceof java.util.Date) {
			java.util.Date d = (java.util.Date) value;
			long l = d.getTime();
			rs.updateDate(parmno, new Date(l));
		} else if (value instanceof DateData) {
			rs.updateDate(parmno, ((DateData) value).getData());
		} else if (value instanceof FastNIBase) {
			Date d = ((FastNIBase) value).toEncodedDate();
			rs.updateDate(parmno, d);
		} else {
			Date d = Date.valueOf(value.toString());
			rs.updateDate(parmno, d);
		}
		logParm(rs, parmno);
	}

	/**
	 * Updates a ResultSet double parameter to the passed value and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBDouble(final ResultSet rs, final int parmno, final double value) throws SQLException {
		rs.updateDouble(parmno, value);
		logParm(rs, parmno);
	}

	/**
	 * Updates a ResultSet int parameter to the passed value and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBInt(final ResultSet rs, final int parmno, final int value) throws SQLException {
		rs.updateInt(parmno, value);
		logParm(rs, parmno);
	}

	/**
	 * Updates a ResultSet long parameter to the passed value and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBLong(final ResultSet rs, final int parmno, final long value) throws SQLException {
		rs.updateLong(parmno, value);
		logParm(rs, parmno);
	}

	/**
	 * Updates a ResultSet parameter to the passed value and logs the value in a StringBuffer which is used for tracing
	 * and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent. This method
	 * looks at the passed variable, and if it has a precision greater than 15 uses getBigDecimal, otherwise it
	 * delegates thework to setDBLong, setDBInt or setDBDouble.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBNumber(final ResultSet rs, final int parmno, final FastNIBase value) throws SQLException {
		if (value.getPrecision() > Num.I15) {
			BigDecimal bd = value.getbigdata();
			rs.updateBigDecimal(parmno, bd);
		} else if (value.getScaleOrZero() == 0 && value.getPrecision() > Num.I10) {
			updateDBLong(rs, parmno, value.toLong());
		} else if (value.getScaleOrZero() == 0) {
			updateDBInt(rs, parmno, value.toInt());
		} else {
			updateDBDouble(rs, parmno, value.toDouble());
		}
	}

	/**
	 * See {@link #updateDBString(ResultSet, int, Object, int)}. Equivalent to that method with the 4th parameter zero.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @throws SQLException on error.
	 */
	public void updateDBString(final ResultSet rs, final int parmno, final Object value) throws SQLException {
		updateDBString(rs, parmno, value, 0);
	}

	/**
	 * Updates a ResultSet String parameter to the passed value and logs the value in a StringBuffer which is used for
	 * tracing and debugging purposes. Requires updateRow(rs) to be called if the changes are to become permanent. The
	 * passed String has trailing spaces removed.
	 *
	 * @param rs passed result set in which a column is to be updated.
	 * @param parmno which column number to update.
	 * @param value new value for that column.
	 * @param len - length of the value expected by the database. If provided (non-zero), the parameter will be padded
	 *            out to this length provided that Appconfig.truncateSQLParms is not "YES".
	 * @throws SQLException on error.
	 */
	public void updateDBString(final ResultSet rs, final int parmno, final Object value, final int len) throws SQLException {
		if (value == null) {
			rs.updateString(parmno, null);
		} else if (getAppConfig().truncateSQLParms) {
			rs.updateString(parmno, QPUtilities.trimRight(value.toString()));
		} else if (len != 0) {
			rs.updateString(parmno, QPUtilities.padRight(value.toString(), len));
		} else {
			rs.updateString(parmno, value.toString());
		}
		logParm(rs, parmno);
	}

	/**
	 * Dummy method that wiil be overridden in SmartAppVars for checking whether
	 * property is mandatory or optional.	 *
	 */
	protected void verifyAppConfig() {

	}

	/**
	 * Utility method to suspend for a time.
	 *
	 * @param l milliseconds to wait.
	 */
	public synchronized void waitabit(final long l) {
		try {
			wait(l);
		} catch (InterruptedException e) {
			// Required, but always ignored error block.
			doNothing();
		}
	}

	public void setSystemMenu(Object[] objects) {
		systemMenu	= objects;
	}
	
	public void setSystemOtherMenu(Object[] objects) {
		systemOtherMenu	= objects;
	}
	
	public void setOtherMenuActive(String othernemu) {
		otherMenuActive = othernemu;
	}
		
	public void setMasterMenu(HashMap<String, String> map) {
		masterMenu = map;
	}
	
	public void setMasterOtherMenu(HashMap<String, String> map) {
		masterOtherMenu = map;

	}


	public void setSubMenu(String subMenu) {
		this.subMenu = subMenu;

	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isDefaultNSL() {
		return isDefaultNSL;
	}

	public void setDefaultNSL(boolean isDefaultNSL) {
		this.isDefaultNSL = isDefaultNSL;
	}

	public void addTableSchema(String tableName, String tableSchema)
	{
		if (null == tableSchemas)
		{
			tableSchemas = new Hashtable<String, String>();

		}
		tableSchemas.put(tableName, tableSchema);

		LOGGER.debug("tableSchema[" + tableSchemas + "]");
	}

	public void addTableSchema(String tableFullName)
	{
		int index = tableFullName.indexOf(".");

		String tableSchema = "";

		String tableName = tableFullName;

		if (index > 0) {

			tableSchema = tableFullName.substring(0, index).trim();

			tableName = tableFullName.substring(index + 1, tableFullName.length()).trim();
		}

		if (null == tableSchemas)
		{
			tableSchemas = new Hashtable<String, String>();

		}

		tableSchemas.put(tableName, tableSchema);

		LOGGER.debug("tableSchema[" + tableSchemas + "]");
	}

	public String getTableSchema(String tableName)
	{
		String tableSchema = "";

		if (!tableSchemas.containsKey(tableName))
		{
			if (QPBaseDataSource.DATABASE_SQLSERVER.equals(getAppConfig().getDatabaseType()))
			{
				tableName = "#" + tableName; // temp tables (tempdb) always start with #
			}
		}

		if (tableSchemas.containsKey(tableName))
			return tableSchemas.get(tableName);

		return tableSchema;
	}

	public void removeTableSchemas()
	{
		if (null != tableSchemas)
			tableSchemas.clear();
	}

	public Hashtable<String, String> getTableSchemas() {
		return tableSchemas;
	}

	public void setTableSchemas(Hashtable<String, String> cachedTableSchema) {
		this.tableSchemas = cachedTableSchema;
	}

	public boolean isTableSchemaEnabled() {
		return isTableSchemaEnabled;
	}

	public void setTableSchemaEnabled(boolean isTableSchemaEnabled) {
		this.isTableSchemaEnabled = isTableSchemaEnabled;
	}
	
	public boolean isEndScreen() {
		return isEndScreen;
	}

	public void setEndScreen(boolean isEndScreen) {
		this.isEndScreen = isEndScreen;
	}

}
