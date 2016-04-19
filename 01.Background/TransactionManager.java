package com.quipoz.COBOLFramework.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quipoz.COBOLFramework.common.exception.ExtMsgException;
import com.quipoz.COBOLFramework.dataarea.DataArea;
import com.quipoz.COBOLFramework.database.QPBaseDataSource;
import com.quipoz.COBOLFramework.database.QPPooledDataSource;
import com.quipoz.COBOLFramework.database.sqlsupport.DatabaseCommon;
import com.quipoz.COBOLFramework.database.sqlsupport.DatabaseCommonFactory;
import com.quipoz.COBOLFramework.job.JobInfo;
import com.quipoz.COBOLFramework.messaging.MessageAgent;
import com.quipoz.COBOLFramework.messaging.MessageType;
import com.quipoz.COBOLFramework.printing.PrintManager;
import com.quipoz.ISeriesFramework.SystemValues.SystemValueRepository;
import com.quipoz.framework.datatype.BaseData;
import com.quipoz.framework.datatype.BaseScreenData;
import com.quipoz.framework.datatype.BinaryData;
import com.quipoz.framework.datatype.FieldType;
import com.quipoz.framework.datatype.FixedLengthStringData;
import com.quipoz.framework.datatype.Indicator;
import com.quipoz.framework.datatype.IntegerData;
import com.quipoz.framework.datatype.PackedDecimalData;
import com.quipoz.framework.datatype.RPGDateData;
import com.quipoz.framework.datatype.RPGIntegerData;
import com.quipoz.framework.datatype.StringBase;
import com.quipoz.framework.datatype.StringData;
import com.quipoz.framework.datatype.ZonedDecimalData;
import com.quipoz.framework.exception.ServerException;
import com.quipoz.framework.util.AppVars;
import com.quipoz.framework.util.CallableProgram;
import com.quipoz.framework.util.FinalizingUtils;
import com.quipoz.framework.util.QPUtilities;
import com.quipoz.framework.util.StringArrayList;
import com.quipoz.framework.util.jdbc.SQLRuntimeException;

/**
 * AppVars has been extended to contain COBOL specific framework variables.
 * 
 * @author Manually modify - Max Wang (CSC) 28 March 2008 To fix bug
 *         133,Implement CANCEL statement.
 * @author wayne.yang
 * @version 1.1 2008-04-02 To fix bug 623 and 639. The bugs are about message
 *          displaying. Reason: a string of length 7 is passed into sql query
 *          string, which requires a length of 8(for msgid case). And also
 *          msgfile requires a length of 10(see the code for details).
 * @author David.Dong 2008-04-16 17:14 change the qtempConnection attribute for
 *         batch.
 */

public class COBOLAppVars extends AppVars {

    public static final String ADD = "*ADD";

    public static final String AFPDS = "*AFPDS";

    public static final String AFTER = "*AFTER";

    public static final String ALL = "*ALL";

    public static final String ATR = "*ATR";

    public static final String BEFORE = "*BEFORE";

    public static final String CALC = "*CALC";

    public static final String CALLLVL = "*CALLLVL";

    public static final String CC = "*CC";

    public static final String CHANGE = "*CHANGE";

    public static final String COMP = "*COMP";

    /**
     * An attribute indicate if convert invalid number when converting string to
     * EBCDIC number is turned on.
     */
    protected static boolean convertInvalidNumber = true;

    public static final String CRLF = "*CRLF";

    public static final String CRTDFT = "*CRTDFT";

    public static final String CURLIB = "*CURLIB";

    public static final String CURRENT = "*CURRENT";

    private static final String DAM_SUFFIX = "TableDAM";

    public static final int DAYS = Calendar.DAY_OF_MONTH;

    public static final String DEVD = "*DEVD";

    public static final String DIAG = "*DIAG";

    public static final String DLM = "*DLM";

    public static final String DOLLARCMD = "$VALIDCOMMANDKEYS";

    public static final String DRAFT = "*DRAFT";

    public static final String DROP = "*DROP";

    public static final String ESCAPE = "*ESCAPE";

    public static final String FB = "*FB";

    public static final String FCFC = "*FCFC";

    public static final String FILE = "*FILE";

    public static final String FILEEND = "*FILEEND";

    public static final String FIRST = "*FIRST";

    public static final String FIXED = "*FIXED";

    public static final String FLDDFT = "*FLDDFT";

    public static final String FROMMBR = "*FROMMBR";

    public static final int HOURS = Calendar.HOUR;

    public static final String INFO = "*INFO";

    public static final String INP = "*INP";

    public static final String INQ = "*INQ";

    public static final String JOBD = "*JOBD";

    public static final String JOBEND = "*JOBEND";

    public static final String KEY = "*KEY";

    public static final String KEYED = "*KEYED";

    public static final String LAST = "*LAST";

    public static final String LASTMBR = "*LASTMBR";

    public static final String LIBL = "*LIBL";

    private static final Logger LOGGER = LoggerFactory.getLogger(COBOLAppVars.class);

    public static final String MAP = "*MAP";

    public static final String MBR = "*MBR";

    public static final String MBRLIST = "*MBRLIST";

    public static final int MINUTES = Calendar.MINUTE;

    public static final int MONTHS = Calendar.MONTH;

    public static final int MSECONDS = Calendar.MILLISECOND;

    public static final String MSG = "*MSG";

    public static final String NOCHK = "*NOCHK";

    public static final String NOMAX = "*NOMAX";

    public static final String NONE = "*NONE";

    private static final Indicator nullind = new Indicator();

    public static final String OBJMGT = "*OBJMGT";

    /**
     * Refers to the 8th entry in (@link #overriddenTables} which is Inhibit
     * Write entry if any.
     */
    public static final int ortInhibitWrite = 7;

    /**
     * Refers to the 3rd entry in (@link #overriddenTables} which is the member
     * name if any.
     */
    public static final int ortMember = 2;

    /**
     * Refers to the 7th entry in (@link #overriddenTables} which is Position
     * entry if any.
     */
    public static final int ortPosition = 6;

    /**
     * Refers to the 6th entry in (@link #overriddenTables} which is Scope entry
     * if any.
     */
    public static final int ortScope = 5;

    /**
     * Refers to the 4th entry in (@link #overriddenTables} which is the Share
     * if any.
     */
    public static final int ortShare = 3;

    /**
     * Refers to the 5th entry in (@link #overriddenTables} which is currently
     * open share count if any.
     */
    public static final int ortShareOpen = 4;

    /**
     * Refers to the first entry in (@link #overriddenTables} which is the new
     * file name if any.
     */
    public static final int ortToFile = 0;

    /**
     * Refers to the 2nd entry in (@link #overriddenTables} which is additional
     * WHERE code if any.
     */
    public static final int ortWhere = 1;

    public static final String OUTFILE = "*OUTFILE";

    public static final String OUTQ = "*OUTQ";

    /**
     * Number of interesting facts we keep about overridden tables. See (@link
     * #overriddenTables}.
     */
    public static final int overriddenSize = 8;

    public static final String PCASCII = "*PCASCII";

    public static final String PF = "*PF";

    // 90 should be the indicator no of the pagedown key
    public static String PFKEY_PAGEDOWN = "PFKey90";

    public static String PFKEY_PAGEUP = "PFKey91";

    public static final String PGMQ = "*PGMQ";

    public static final String PRV = "*PRV";

    public static final String PUBLIC = "*PUBLIC";

    public static final String QCENTURY = "QCENTURY";

    public static final String QDATE = "QDATE";

    public static final String QDATFMT = "QDATFMT";

    public static final String QDAY = "QDAY";

    public static final String QHOUR = "QHOUR";

    public static final String QMONTH = "QMONTH";

    public static final String QTEMP = "QTEMP";

    public static final String QTIME = "QTIME";

    public static final String QUTCOFFSET = "QUTCOFFSET";

    public static final String QYEAR = "QYEAR";

    /**
     * Holds the following information about Screen Records and subfiles. First
     * and last row on the screen, first and last column on the screen. Static
     * as it doesn't change from screen to screen. The key is record name, the
     * information a 4 element int array.
     * <p>
     * Element 0 = top row <br>
     * 1 = bottom row <br>
     * 2 = leftmost column <br>
     * 3 = rightmost column
     */
    public transient static Hashtable recordSizes = new Hashtable(2000);

    public static final String REPLACE = "*REPLACE";

    private static final String ROLLDOWN = "$ROLLDOWN";

    private static final String ROLLUP = "$ROLLUP";

    private static final String ROUTINE = QPUtilities.getThisClass();

    public static final String RPGBLANK = StringBase.DEFAULT;

    public static final int RPGZERO = 0;

    public static final String RQS = "*RQS";

    public static final String RWX = "*RWX";

    public static final String SAME = "*SAME";

    public static final String SECLVL = "*SECLVL";

    public static final int SECONDS = Calendar.SECOND;

    public static final String SECURE = "*SECURE";

    public static final String SL = "*SL";

    public static final String STAR_NO = "*NO";

    public static final String STAR_YES = "*YES";

    public static final String STATUS = "*STATUS";

    public static final String STD = "*STD";

    public static final String STDASCII = "*STDASCII";

    public static final String STMF = "*STMF";

    public static final String SYSOPR = "*SYSOPR";

    public static final String SYSRPYL = "*SYSRPYL";

    /**
     * Holds references to table metadata for use in FileCode.
     */
    private transient static Hashtable tableMetaData = new Hashtable(500);

    public static final String UNLOAD = "*UNLOAD";

    public static final String USE = "*USE";

    public static final String USRPRF = "*USRPRF";

    public static final long YEAR2000 = Date.valueOf("2000-01-01").getTime();

    public static final int YEARS = Calendar.YEAR;

    /**
     * Check if all indicators specified is on
     * 
     * @param inds
     * @return
     */
    private static final boolean isAllIndicatorsOn(final Indicator[] inds) {
        if (inds == null || inds.length == 0) {
            return false;
        }

        boolean result = true;
        for (Indicator ind : inds) {
            if (ind == null || ind.isOff()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Check if convert invalid number when converting string to ebcdic number
     * 
     * @return
     */
    public static boolean isConvertInvalidNumber() {
        return convertInvalidNumber;
    }

    public static void main(final String[] args) {
        new COBOLAppVars();
    }

    /**
     * <p>
     * The purpose of this function is to implement RTVSYSVAL command.<br>
     * Only the following system values are retrievable:
     * <ol>
     * <li>QDATE: the current system date, default format in iSeries is
     * MonthDayYear (6 characters) or yydd 5 characters.
     * <li>QTIME: the current system time, format is HHmmss + millionths of a
     * second (up to 12 characters). Note, implemented via timestamp, as written
     * values past milliseconds will be zero. This could be fixed in Java 1.5.
     * <li>QUTCOFFSET: the current offset from GMT, format is + or - HHmm (0 to
     * 24 hour and 0 to 59 minute)
     * <li>QMONTH: the current system month, format is 2 character value from 01
     * to 12.
     * <li>QYEAR: the current system year, format is the last 2 character of the
     * year (i.e 00 - 99).
     * <li>QDAY: the current system day, format is 2 character day of the month
     * (i.e. 01 - 31).
     * <li>QDATFMT: the format of the QDATE returned, this is always MDY
     * (currently only used to check the original date format and in this case
     * it's always MDY).
     * <li>QHOUR: the current system hour, in 24 hour format with 2 characters
     * long.
     * </ol>
     * The content is moved to another utility class. It is not supposed to be
     * in the global vars. Max W.
     * 
     * @param sysval
     *            Specifies the system value to be retrieved
     * @param returnValue
     *            Returns the system value
     */
    public static void retrieveSystemValue(final Object osysval, final BaseData returnValue) {
        SystemValueRepository.retrieveSystemValue(osysval, returnValue);
    }

    /**
     * Indicator 00-100. These indicators are declared as public because they
     * are Boolean, always will be Boolean, and are referenced constantly.
     * Encapsulating them with get and set methods would be pointless.
     * <p>
     * They are declared as Boolean rather than boolean so they are Objects, and
     * can be present both in an array and standalone. This is useful when we
     * need to set lots of them.
     */
    public Indicator ind00 = new Indicator();

    public Indicator ind01 = new Indicator();

    public Indicator ind02 = new Indicator();

    public Indicator ind03 = new Indicator();

    public Indicator ind04 = new Indicator();

    public Indicator ind05 = new Indicator();
    public Indicator ind06 = new Indicator();
    public Indicator ind07 = new Indicator();
    public Indicator ind08 = new Indicator();
    public Indicator ind09 = new Indicator();
    public Indicator ind10 = new Indicator();
    public Indicator ind11 = new Indicator();
    public Indicator ind12 = new Indicator();

    public Indicator ind13 = new Indicator();

    public Indicator ind14 = new Indicator();

    public Indicator ind15 = new Indicator();

    public Indicator ind16 = new Indicator();

    public Indicator ind17 = new Indicator();

    public Indicator ind18 = new Indicator();

    public Indicator ind19 = new Indicator();

    public Indicator ind20 = new Indicator();

    public Indicator ind21 = new Indicator();

    public Indicator ind22 = new Indicator();

    public Indicator ind23 = new Indicator();

    public Indicator ind24 = new Indicator();

    public Indicator ind25 = new Indicator();

    public Indicator ind26 = new Indicator();

    public Indicator ind27 = new Indicator();

    public Indicator ind28 = new Indicator();

    public Indicator ind29 = new Indicator();

    public Indicator ind30 = new Indicator();

    public Indicator ind31 = new Indicator();

    public Indicator ind32 = new Indicator();

    public Indicator ind33 = new Indicator();

    public Indicator ind34 = new Indicator();

    public Indicator ind35 = new Indicator();

    public Indicator ind36 = new Indicator();

    public Indicator ind37 = new Indicator();

    public Indicator ind38 = new Indicator();

    public Indicator ind39 = new Indicator();

    public Indicator ind40 = new Indicator();

    public Indicator ind41 = new Indicator();

    public Indicator ind42 = new Indicator();

    public Indicator ind43 = new Indicator();

    public Indicator ind44 = new Indicator();

    public Indicator ind45 = new Indicator();

    public Indicator ind46 = new Indicator();
    public Indicator ind47 = new Indicator();
    public Indicator ind48 = new Indicator();
    public Indicator ind49 = new Indicator();
    public Indicator ind50 = new Indicator();
    public Indicator ind51 = new Indicator();

    public Indicator ind52 = new Indicator();

    public Indicator ind53 = new Indicator();

    public Indicator ind54 = new Indicator();

    public Indicator ind55 = new Indicator();

    public Indicator ind56 = new Indicator();

    public Indicator ind57 = new Indicator();

    public Indicator ind58 = new Indicator();

    public Indicator ind59 = new Indicator();

    public Indicator ind60 = new Indicator();

    public Indicator ind61 = new Indicator();

    public Indicator ind62 = new Indicator();

    public Indicator ind63 = new Indicator();

    public Indicator ind64 = new Indicator();

    public Indicator ind65 = new Indicator();

    public Indicator ind66 = new Indicator();

    public Indicator ind67 = new Indicator();

    public Indicator ind68 = new Indicator();

    public Indicator ind69 = new Indicator();

    public Indicator ind70 = new Indicator();

    public Indicator ind71 = new Indicator();

    public Indicator ind72 = new Indicator();

    public Indicator ind73 = new Indicator();

    public Indicator ind74 = new Indicator();

    public Indicator ind75 = new Indicator();

    public Indicator ind76 = new Indicator();

    public Indicator ind77 = new Indicator();

    public Indicator ind78 = new Indicator();

    public Indicator ind79 = new Indicator();

    public Indicator ind80 = new Indicator();

    public Indicator ind81 = new Indicator();

    public Indicator ind82 = new Indicator();

    public Indicator ind83 = new Indicator();

    public Indicator ind84 = new Indicator();

    public Indicator ind85 = new Indicator();

    public Indicator ind86 = new Indicator();

    public Indicator ind87 = new Indicator();

    public Indicator ind88 = new Indicator();

    public Indicator ind89 = new Indicator();

    public Indicator ind90 = new Indicator();

    public Indicator ind91 = new Indicator();

    public Indicator ind92 = new Indicator();

    public Indicator ind93 = new Indicator();

    public Indicator ind94 = new Indicator();

    public Indicator ind95 = new Indicator();

    public Indicator ind96 = new Indicator();

    public Indicator ind97 = new Indicator();

    public Indicator ind98 = new Indicator();

    public Indicator ind99 = new Indicator();

    public Indicator indh1 = new Indicator();

    public Indicator indlr = new Indicator();

    /* RPG Overflow indicator set */
    public Indicator indoa = new Indicator();

    public Indicator indob = new Indicator();

    public Indicator indoc = new Indicator();

    public Indicator indod = new Indicator();

    public Indicator indoe = new Indicator();

    public Indicator indof = new Indicator();

    public Indicator indog = new Indicator();

    public Indicator indov = new Indicator();

    /**
     * Additional PFKeys that are valid on a screen to be presented. These are
     * set in ScreenRecord.write which calls ScreenRecord.clearInds. The
     * additional valid ones are then put in here. This is then picked up in
     * commonScript2.jsp, where it will get cleared.
     */
    public String additionalValidKeys = "";

    /**
     * Holds references to arrays which is needed because RPG can search an
     * array and then some time later use the index into the array that was
     * found. The index is not a separate variable.
     */
    public transient Hashtable arrays = new Hashtable();

    public Indicator Clancy = indof;

    /**
     * Global error indicator; set for any file operation with an extender.
     */
    public boolean COBOLFileError = false;

    /**
     * Data area table representing the users data area
     */
    public transient DataArea dataarea = new DataArea();

    public ZonedDecimalData date = new ZonedDecimalData(8);

    public ZonedDecimalData day = new ZonedDecimalData(2);

    /**
     * A hashtable containing the ids of messages already displayed by the
     * addDBMessage service.
     */
    private Hashtable displayedMessages = new Hashtable();

    public boolean dontReshowMessages = false;

    public Indicator editError = ind30;

    /**
     * Global end of file indicator; set for any file operation of the following
     * types:
     * <ul compact>
     * <li>readnext
     * <li>readnextequal
     * <li>readprevious
     * <li>readpreviousequal
     * </ul>
     * Reset to false if the following succeed
     * <ul compact>
     * <li>open
     * <li>readequal
     * <li>set greater
     * <li>set less
     * </ul>
     */
    public boolean eof = false;

    /**
     * Global exact match flag. Set on if the last setll, lookup found exact
     * match.
     */
    public boolean exactMatch = false;

    /**
	 */
    public Indicator f1Help = ind01;

    public Indicator f3Exit = ind03;

    public Indicator f4RefData = ind04;

    public Indicator fieldAttribute70 = ind70;

    public Indicator fieldAttribute71 = ind71;

    public Indicator fieldAttribute72 = ind72;

    public Indicator fieldAttribute73 = ind73;

    public Indicator fieldAttribute74 = ind74;

    public Indicator fieldAttribute75 = ind75;

    public Indicator fieldAttribute76 = ind76;

    public Indicator fieldAttribute77 = ind77;

    public Indicator fieldAttribute78 = ind78;

    public Indicator fieldAttribute79 = ind79;

    /**
     * Stores information about screen fields that need to have changes detected
     * and set an indicator when they do. This is indicated in an iSeries DSPF
     * as
     * <p>
     * SCPLAT 6A B 5 23CHANGE(55)
     * <p>
     * That is, indicator 55 will be set on if the field is changed by the user,
     * off otherwise.
     * <p>
     * In this implementation, "change" will mean that the value is physically
     * different. This may not be exactly the same as on the iSeries, where
     * "change" might be the user changes the value from 0 to 0.0 but will have
     * the same effect.
     * <p>
     * The content of the HashMap will be
     * <p>
     * key - the name of the field concerned, no leading or trailing spaces,
     * case sensitive, String.
     * <p>
     * value - two element Object array. First element is a String containing
     * what the field's toString() method returned before, the second an
     * Indicator.
     * <p>
     * This HashMap will be accessed in displayScreen in RPGConVCodeModel.
     * <ol>
     * <li>Before the screen is shown, the HashMap will be cleared.
     * <li>The JSP will write fields, values and indicators to it as they are
     * encountered. The reason for this is, we do not process change
     * notification requests unless they are in a record that is actually
     * displayed.
     * <li>This will save the field name, value, indicator.
     * <li>On return, the HashMap will be scanned and any indicators set/cleared
     * as appropriate.
     * </ol>
     */
    private Hashtable<String, Object> fieldChangeIndicators = new Hashtable<String, Object>();

    public Indicator fieldError31 = ind31;

    public Indicator fieldError32 = ind32;

    public Indicator fieldError33 = ind33;

    public Indicator fieldError34 = ind34;

    public Indicator fieldError35 = ind35;

    public Indicator fieldError36 = ind36;

    public Indicator fieldError37 = ind37;

    public Indicator fieldError38 = ind38;

    public Indicator fieldError39 = ind39;

    public Indicator fieldError40 = ind40;

    public Indicator fieldError41 = ind41;

    public Indicator fieldError42 = ind42;

    public Indicator fieldError43 = ind43;

    public Indicator fieldError44 = ind44;

    public Indicator fieldError45 = ind45;

    public Indicator fieldError46 = ind46;

    public Indicator fieldError47 = ind47;

    public Indicator fieldError48 = ind48;

    public Indicator fieldError49 = ind49;

    public Indicator fieldError50 = ind50;

    public Indicator fieldError51 = ind51;

    public Indicator fieldError52 = ind52;

    public Indicator fieldError53 = ind53;

    public Indicator fieldError54 = ind54;

    public Indicator fieldError55 = ind55;

    public Indicator fieldError56 = ind56;

    public Indicator fieldError57 = ind57;

    public Indicator fieldError58 = ind58;

    public Indicator fieldError59 = ind59;

    public Indicator fieldError60 = ind60;

    public Indicator fieldError61 = ind61;

    public Indicator fieldError62 = ind62;

    public Indicator fieldError63 = ind63;

    public Indicator fieldError64 = ind64;

    public Indicator fieldError65 = ind65;

    public Indicator fieldError66 = ind66;

    public Indicator fieldError67 = ind67;

    public Indicator fieldError68 = ind68;

    public Indicator fieldError69 = ind69;

    public Indicator fileError = ind22;

    public Indicator firstTime = ind20;

    public Indicator functionKey10 = ind10;

    public Indicator functionKey11 = ind11;

    public Indicator functionKey12 = ind12;

    public Indicator functionKey13 = ind13;

    public Indicator functionKey14 = ind14;

    public Indicator functionKey15 = ind15;

    public Indicator functionKey16 = ind16;

    public Indicator functionKey17 = ind17;

    public Indicator functionKey18 = ind18;

    public Indicator functionKey19 = ind19;

    public Indicator functionKey2 = ind02;

    public Indicator functionKey20 = ind20;

    public Indicator functionKey21 = ind21;

    public Indicator functionKey22 = ind22;

    public Indicator functionKey23 = ind23;

    public Indicator functionKey24 = ind24;

    public Indicator functionKey5 = ind05;

    public Indicator functionKey6 = ind06;

    public Indicator functionKey7 = ind07;

    public Indicator functionKey8 = ind08;

    public Indicator functionKey9 = ind09;

    public Indicator generalWork90 = ind90;

    public Indicator generalWork91 = ind91;

    public Indicator generalWork92 = ind92;

    public Indicator generalWork93 = ind93;

    public Indicator generalWork94 = ind94;

    public Indicator generalWork95 = ind95;

    public Indicator generalWork96 = ind96;

    public Indicator generalWork97 = ind97;

    public Indicator generalWork98 = ind98;

    public Indicator generalWork99 = ind99;

    private transient Session hibernateSession = null;

    public Indicator[] IndicArea = new Indicator[] { ind00, ind01, ind02, ind03, ind04, ind05, ind06, ind07, ind08,
            ind09, ind10, ind11, ind12, ind13, ind14, ind15, ind16, ind17, ind18, ind19, ind20, ind21, ind22, ind23,
            ind24, ind25, ind26, ind27, ind28, ind29, ind30, ind31, ind32, ind33, ind34, ind35, ind36, ind37, ind38,
            ind39, ind40, ind41, ind42, ind43, ind44, ind45, ind46, ind47, ind48, ind49, ind50, ind51, ind52, ind53,
            ind54, ind55, ind56, ind57, ind58, ind59, ind60, ind61, ind62, ind63, ind64, ind65, ind66, ind67, ind68,
            ind69, ind70, ind71, ind72, ind73, ind74, ind75, ind76, ind77, ind78, ind79, ind80, ind81, ind82, ind83,
            ind84, ind85, ind86, ind87, ind88, ind89, ind90, ind91, ind92, ind93, ind94, ind95, ind96, ind97, ind98,
            ind99 };

    public Indicator indka = ind01;

    public Indicator indkb = ind02;

    public Indicator indkc = ind03;

    public Indicator indkd = ind04;

    public Indicator indke = ind05;

    public Indicator indkf = ind06;

    public Indicator indkg = ind07;

    public Indicator indkh = ind08;

    public Indicator indki = ind09;

    public Indicator indkj = ind10;

    public Indicator indkk = ind11;

    public Indicator indkl = ind12;

    public Indicator indkm = ind13;

    public Indicator indkn = ind14;

    public Indicator indko = ind15;

    public Indicator indkp = ind16;

    public Indicator indkq = ind17;

    public Indicator indkr = ind18;

    public Indicator indks = ind19;

    public Indicator indkt = ind20;

    public Indicator indku = ind21;

    public Indicator indkv = ind22;

    public Indicator indkw = ind23;

    public Indicator indkx = ind24;

    public Indicator indky = ind25;

    public Indicator indkz = ind26;

    /**
     * Area to implement the required parts of RPG Screen File INFDS. At the
     * time of writing, this only included the cursor position.
     */
    public FixedLengthStringData screenInfds = new FixedLengthStringData(384);

    public RPGIntegerData intCursorX = new RPGIntegerData(5).isAPartOf(screenInfds, 371, FieldType.NON_DECLARATION);

    public RPGIntegerData intCursorY = new RPGIntegerData(5).isAPartOf(screenInfds, 369, FieldType.NON_DECLARATION);

    public RPGIntegerData intCursorYX = new RPGIntegerData(10).isAPartOf(screenInfds, 369, FieldType.NON_DECLARATION);

    /**
     * Job Information including:
     * <ol>
     * <li>TODO = user profile name / user id
     * <li>OUTQ = output queue
     * <li>OUTQLIB = library that contains the output queue
     * <li>JOBD = job description
     * <li>JOBDLIB = library that contains the job description
     * <ol>
     */
    private JobInfo jobInfo;

    public Indicator jspHelp = ind27;

    // According to Keymap, rollup/pagedown seems using indicator 90
    public Indicator jspPageDown = ind90;

    // According to Keymap, rollup/pagedown seems using indicator 91
    public Indicator jspPageUp = ind91;

    public Indicator jspSFL87 = ind87;

    public Indicator jspSFL88 = ind88;

    public Indicator jspSFL89 = ind89;

    public Indicator jspSFLClear = ind83;

    public Indicator jspSFLDisplay = ind80;

    public Indicator jspSFLDisplayControl = ind81;

    public Indicator jspSFLEnd = ind84;

    public Indicator jspSFLInitialise = ind82;

    public Indicator jspSFLMessageIdentifier = ind85;

    public Indicator jspSFLNextChange = ind86;

    public Indicator jspValidFunctionKey = ind26;

    public Indicator jspVarChange = ind25;

    /**
     * Local data area. Defined as 2048 bytes but you could increase it if
     * required as it is public.
     */
    public FixedLengthStringData lda = new FixedLengthStringData(2048);

    /**
     * Implements the RPG concept of a library list. The contents of this
     * ArrayList should ALWAYS refelect the contents of the library list in the
     * currently executing program. This is done by
     * <ol>
     * <li>Whenever a new RPGCodeModel(conv or nonconv) starts, it contains its
     * own library list. This is initialised from the COBOLAppVars one.
     * <li>While it is active, any changes are reflected in both instances.
     * <li>When it ends, in all cases - abend or not - it returns to whatever
     * called it. This eventually returns control to the routine that does the
     * call, in the RPGCodeModel. That must then restore the COBOLAppVars
     * contents from its own store (effectively a stack).
     */
    private StringArrayList libraryList = new StringArrayList();

    /**
     * Implements the RPG concept of globally monitored messages. Messages
     * logged in here will not result in an exception as long as they are raised
     * via the addEscapeMessage method in this Class.
     * <ol>
     * <li>Whenever a new RPGCodeModel(conv or nonconv) starts, it contains its
     * own list. This is initialised from the COBOLAppVars one.
     * <li>While it is active, any changes are reflected in both instances.
     * <li>When it ends, in all cases - abend or not - it returns to whatever
     * called it. This eventually returns control to the routine that does the
     * call, in the RPGCodeModel. That must then restore the COBOLAppVars
     * contents from its own store (effectively a stack).
     */
    private Hashtable monitoredMessages = new Hashtable();

    public ZonedDecimalData month = new ZonedDecimalData(2);

    /**
     * Contains the number of rows processed in the last sql statement.
     */
    protected transient int numberOfRowsProcessed = -1;

    /**
     * Holds overrides to tables used in FileCode. A table can be overridden in
     * two ways:
     * <ol>
     * <li>The name can be changed. Hopefully, to the name of another table that
     * has the same columns otherwise the SQL will fail! The change remains
     * extant for the current user/session until it is removed.
     * <li>An additional bit of SQL can be added to the WHERE clause. Eg,
     * " PRODUCT_TYPE = 'Q'". This will be added to the SQL used to access the
     * file. It's up to the provider to make sure the SQL is syntactically
     * correct.
     * </ol>
     */
    private transient Hashtable overriddenTables = new Hashtable();

    /**
     * A HashMap that contains all the current printer files as HashMaps i.e.
     * printerFiles - > HashMap<key(String filename), value(HashMap
     * printerFileAttr)> printerFileAttr - > HashMap<key(String attribute),
     * value(String attrValue)>
     */
    private HashMap printerFiles = new HashMap();

    /**
     * Program Status Area
     */
    public ISeriesProgStat progds = new ISeriesProgStat();

    /**
     * Passthrough data. Defined as 1024 bytes but you could increase it if
     * required as it is public.
     */
    public FixedLengthStringData ptd = new FixedLengthStringData(1024);

    /**
     * Reused connection for the temporary file area. Never commit this
     * connection! modified by David.Dong at 2008-04-16 for batch. this db
     * connection should be shared between different Appvars.
     */
    public transient Connection qtempConnection = null;

    /**
     * Global record found flag. Set on if the last setll, setgt, chain or
     * delete on ANY file found a record.
     */
    public boolean recordFound = false;

    public Indicator reserved23 = ind23;

    public Indicator reserved24 = ind24;

    /**
     * Memory resident routines. Note, the contents of this HashMap <font
     * color=blue>are not transferred across the EJB layer</font> as the
     * contents are pointers to instances of memory resident routines which are
     * not required on the Web side. Specific code deliberately does not exist
     * in the "set" method to copy it, and checking is bypassed in the sanity
     * check "static" block.
     */
    private transient Map<Object, Object> routines = new HashMap<Object, Object>();

    private final Indicator[] RPGOA = new Indicator[] { indh1, indlr, indka, indkb, indkc, indkd, indke, indkf, indkg,
            indkh, indki, indkj, indkk, indkl, indkm, indkn, indko, indkp, indkq, indkr, indks, indkt, indku, indkv,
            indkw, indkx, indky, indkz, indoa, indob, indoc, indod, indoe, indof, indog, indov };

    public Indicator screenError = ind21;

    public FixedLengthStringData screenFnKey = new FixedLengthStringData(1).isAPartOf(screenInfds, 368,
            FieldType.NON_DECLARATION);

    public RPGIntegerData screenFnKeyi = new RPGIntegerData(3).setUnsigned().isAPartOf(screenInfds, 368,
            FieldType.NON_DECLARATION);

    /**
     * Shared screen models. Note, the contents of this "store" <font
     * color=blue>are not transferred across the EJB layer</font> as the
     * contents are pointers to instances of memory resident
     * <code>*ScreenVars</code> classes which are not required on the Web side.
     * Specific code deliberately does not exist in the "set" method to copy it,
     * and checking is bypassed in the sanity check "static" block.
     * 
     * @see com.quipoz.framework.util.ISeriesScreenVarsSharedStore for detailed
     *      doco.
     */
    transient ISeriesScreenVarsSharedStore sharedScreenVars = new ISeriesScreenVarsSharedStore();

    /**
     * smartFileCodeSessionVariables Map. Store cached table data. It replaces
     * single instance of SmartFileCode in routines key: Table Dame Class.
     * value: smartFileCodeSessionVariables of tableDam
     */
    private transient Map<Object, Object> smartFileCodeSessionVariablesMap = new HashMap<Object, Object>();

    private transient int sqlCode = 0;

    private transient SQLException sqlError = null;

    public FixedLengthStringData strCursorX = new FixedLengthStringData(2).isAPartOf(screenInfds, 371,
            FieldType.NON_DECLARATION);

    public FixedLengthStringData strCursorY = new FixedLengthStringData(2).isAPartOf(screenInfds, 369,
            FieldType.NON_DECLARATION);

    /**
     * Implements the iSeries QTEMP tables list. It is necessary to log (and
     * remove) any tables and views created in the session (and deleted) because
     * this information is not available from the Catalog Metadata. therefore,
     * you can't easily tell at a later date if you have in fact created the
     * table. It is essential, of course, to keep this list up to date!
     */
    protected transient StringArrayList temporaryTables = new StringArrayList();

    private String terminalId = "";

    /**
     * Reusable connection for transaction control. Initially it should be same
     * instance of qtempConnection. It will get a new instance when STRCMTCTL is
     * called. auto commit is set as false at the same time; the conecction will
     * be reassigned to qtempConnection when ENDCMTCTL is called; to ensure the
     * safety of concurrence, the retrieve and upadte of connection has to be
     * synchronized; Transaction states: 1) STARTED - txConnection is not null;
     * 2) ENDED - txConnection is null; 3) PENDING - not sure how to indicate
     * the transaction is pending change yet. Then not to handle this case
     */
    public transient Connection txConnection = null;
    public ZonedDecimalData uDate = new ZonedDecimalData(8);

    public ZonedDecimalData uDay = new ZonedDecimalData(2);

    public ZonedDecimalData uMonth = new ZonedDecimalData(2);

    /**
     * The user programmable switches. These correspond to U1-U8 in RPG, or if
     * you really want to go a long way back to the 8 physical switches there
     * were on the front of the machine in card-punch days.
     * <p>
     * In CL, When you type CHGJOB SWS(00010000) what you've done is set
     * switches 1-3 off (0 means off), set switch 4 on (1 means on), and set
     * switches 5-8 off.
     * <p>
     * You can also do something like this: CHGJOB SWS(XXX1XXXX). In that case,
     * anything with an "X" is unchanged from what the switch was previously set
     * to. In this case, switches 1-3 are unchanged, switch 4 is set on, and
     * switches 5-8 are unchanged. In COBOL the UPSI switches are named UPSI-0
     * through UPSI-7. The Indicator names were changed to correspond to the
     * COBOL reserved names.
     */
    public Indicator upsi0 = new Indicator();

    public Indicator upsi1 = new Indicator();

    public Indicator upsi2 = new Indicator();

    public Indicator upsi3 = new Indicator();

    public Indicator upsi4 = new Indicator();

    public Indicator upsi5 = new Indicator();

    public Indicator upsi6 = new Indicator();

    public Indicator upsi7 = new Indicator();

    /**
     * User profile including:
     * <ol>
     * <li>USERPRF = user profile name / user id
     * <li>OUTQ = output queue
     * <li>OUTQLIB = library that contains the output queue
     * <li>JOBD = job description
     * <li>JOBDLIB = library that contains the job description
     * <ol>
     */
    protected Hashtable userProfile = new Hashtable();

    public ZonedDecimalData uYear = new ZonedDecimalData(2);

    public ZonedDecimalData year = new ZonedDecimalData(4);

    public COBOLAppVars() {
        super();

        RPGDateData today = new RPGDateData(BaseData.ISO_DATE_FORMAT);
        today.setToday();
        date.set(today);
        day.set(today.getDate());
        month.set(today.getMonth());
        year.set(today.getYear());

        RPGDateData uToday = new RPGDateData(BaseData.YMD_FORMAT);
        uToday.setToday();
        uDate.set(uToday);
        uDay.set(uToday.getDate());
        uMonth.set(uToday.getMonth());
        uYear.set(uToday.getYear());
    }

    /**
     * @param pAppName
     */
    public COBOLAppVars(final String pAppName) {
        super(pAppName);

        RPGDateData today = new RPGDateData(BaseData.ISO_DATE_FORMAT);
        today.setToday();
        date.set(today);
        day.set(today.getDate());
        month.set(today.getMonth());
        year.set(today.getYear());

        RPGDateData uToday = new RPGDateData(BaseData.YMD_FORMAT);
        uToday.setToday();
        uDate.set(uToday);
        uDay.set(uToday.getDate());
        uMonth.set(uToday.getMonth());
        uYear.set(uToday.getYear());
    }

    /**
     * {@link #getDBMessage(Object)} is called, and the resultant String passed
     * to {@link AppVars.#addMessage(String)}.
     */
    public void addDBMessage(final Object msgid) {
        String add = getDBMessage(msgid);
        if (add != null && add.trim().length() > 0) {
            addMessage(add);
        }
    }

    /**
     * {@link #getDBMessage(Object, Object)} is called, and the resultant String
     * passed to {@link AppVars.#addMessage(String)}.
     */
    public void addDBMessage(final Object msgid, final Object subst) {
        String add = getDBMessage(msgid, subst);
        if (add != null && add.trim().length() > 0) {
            addMessage(add);
        }
    }

    /**
     * {@link #getDBMessage(Object, Object, Object)} is called, and the
     * resultant String passed to {@link AppVars.#addMessage(String)}.
     */
    public void addDBMessage(final Object msgid, final Object msgfile, final Object subst) {
        String add = getDBMessage(msgid, msgfile, subst);
        if (add != null && add.trim().length() > 0) {
            addMessage(add);
        }
    }

    /**
     * /** {@link #getDBMessage(Object, Object, Object, Indicator)} is called,
     * and the resultant String passed to {@link AppVars.#addMessage(String)}.
     */
    public void addDBMessage(final Object msgid, final Object msgfile, final Object subst, final Indicator indicator) {
        String add = getDBMessage(msgid, msgfile, subst, indicator);
        if (add != null && add.trim().length() > 0) {
            addMessage(add);
        }
    }

    /**
     * Emulates the RPG concept of an external message. This also refers to
     * ExtMsgException. Call this method will either
     * <ol>
     * <li>Throw an ExtMsgException.
     * <li>Not throw one, because for this program a global MONMSG is in force
     * which ignores the particular message id.
     * </ol>
     * 
     * @param messageId
     */
    public void addExtMessage(final String messageId) {
        if (monitoredMessages.get(messageId) != null) {
            return;
        }
        throw new ExtMsgException(messageId);
    }

    /**
     * Emulates the RPG concept of an external message. This also refers to
     * ExtMsgException. Call this method will either
     * <ol>
     * <li>Throw an ExtMsgException.
     * <li>Not throw one, because for this program a global MONMSG is in force
     * which ignores the particular message id.
     * </ol>
     * 
     * @param messageId
     */
    public void addExtMessage(final String messageId, final Exception e) {
        if (monitoredMessages.get(messageId) != null) {
            return;
        }
        throw new ExtMsgException(messageId, e);
    }

    /**
     * Emulates the RPG concept of an external message. This also refers to
     * ExtMsgException. Call this method will either
     * <ol>
     * <li>Throw an ExtMsgException.
     * <li>Not throw one, because for this program a global MONMSG is in force
     * which ignores the particular message id.
     * </ol>
     * 
     * @param messageId
     * @param message
     *            - will be passed on to the exception
     */
    @Override
    public void addExtMessage(final String messageId, final String message) {
        if (monitoredMessages.get(messageId) != null
                && !COBOLConvCodeModel.LOCAL_ENABLE.equals(monitoredMessages.get(messageId))) {
            return;
        }
        throw new ExtMsgException(messageId, message);
    }

    /**
     * See {@link #addLibraryListEntry(String, String)}. Equivalent to calling
     * that with FIRST as the second parameter.
     * 
     * @param pLibrary
     *            The name of the library being added.
     */
    public void addLibraryListEntry(final Object pLibrary) {
        addLibraryListEntry(pLibrary, FIRST);
    }

    /**
     * Equivalent to {@link #addLibraryListEntry(Object)} but sets the passed
     * indicator on if any error occurs.
     * 
     * @param library
     * @param indicator
     */
    public void addLibraryListEntry(final Object library, final Indicator indicator) {
        try {
            addLibraryListEntry(library);
        } catch (Exception e) {
            indicator.setOn();
        }
    }

    /**
     * <p>
     * Implements RPG ADDLIBLE command.
     * <p>
     * Adds a library name to the library list.
     * <p>
     * The following escape messages are implemented:
     * <ol>
     * <li>CPF2103: Library &1 already exists in the library list.
     * </ol>
     * 
     * @param library
     *            The name of the library being added.
     * @param pPosition
     *            The position of the library to be added.
     *            <dl>
     *            <dt>*FIRST</dt> <dd>Added at the top</dd> <dt>*LAST</dt> <dd>
     *            Added at the bottom</dd>
     *            </dl>
     *            By preference, use COBOLAppVars.FIRST etc.
     */
    public void addLibraryListEntry(final Object pLibrary, String pPosition) {
        pPosition = (pPosition == null ? FIRST : pPosition.trim().toUpperCase());

        String library = getTempSchema(pLibrary.toString().trim());

        if (libraryList.contains(library.toString())) {
            addExtMessage("CPF2103", "Library " + library + " already exists in the library list.");
            return;
        }

        if (pPosition.equals(FIRST)) {
            libraryList.add(0, library.toString().trim());
        } else if (pPosition.equals(LAST)) {
            libraryList.add(library.toString().trim());
        } else {
            addExtMessage("CPF2176", "Library " + library + " not added. (Parm is '" + pPosition
                    + "', should be *FIRST/*LAST)");
        }
    }

    /**
     * <p>
     * Implements RPG ADDLIBLE command.
     * <p>
     * Adds a library name to the library list.
     * <p>
     * The following escape messages are implemented:
     * <ol>
     * <li>CPF2103: Library &1 already exists in the library list.
     * <li>CPF2149: Library &1 was not found in the user library list.
     * <li>CPF2176: Library &1 not added.
     * </ol>
     * 
     * @param pLibrary
     *            The name of the library being added.
     * @param pPosition
     *            The position of the library to be added.
     *            <dl>
     *            <dt>BEFORE</dt> <dd>Added before the reference library name
     *            </dd> <dt>AFTER</dt> <dd>Added after the reference library
     *            name</dd> <dt>REPLACE</dt> <dd>Added after the reference
     *            library name</dd>
     *            </dl>
     *            By preference, use COBOLAppVars.BEFORE etc.
     * @param pReference
     *            The library to add before/after or replace.
     */
    public void addLibraryListEntry(final Object pLibrary, String pPosition, String pReference) {

        if (pPosition == null || (pPosition = pPosition.trim().toUpperCase()).length() == 0) {
            pPosition = FIRST;
        }

        if (pPosition.equals(FIRST) || pPosition.equals(LAST)) {
            addLibraryListEntry(pLibrary, pPosition);
            return;
        }

        if (pReference == null || (pReference = pReference.trim().toUpperCase()).length() == 0) {
            pReference = "";
        }

        if (libraryList.contains(pLibrary.toString())) {
            addExtMessage("CPF2103", "Library " + pLibrary + " already exists in the library list.");
            return;
        }

        int indexOf = libraryList.indexof(pReference);

        if (indexOf < 0) {
            addExtMessage("CPF2149", "Library " + pReference + " was not found in the user library list.");
            return;
        }

        if (pPosition.equals(AFTER)) {
            libraryList.add(indexOf + 1, pLibrary.toString().trim());
        } else if (pPosition.equals(BEFORE)) {
            libraryList.add(indexOf, pLibrary.toString().trim());
        } else if (pPosition.equals(REPLACE)) {
            libraryList.set(indexOf, pLibrary.toString().trim());
        } else {
            addExtMessage("CPF2176", "Library " + pLibrary + " not added.");
            return;
        }

    }

    /**
     * Pass the message to AppVars and set the passed indicator off.
     * 
     * @param message
     * @param ind
     */
    public void addMessage(final String message, final Indicator ind) {
        addMessage(message);
        ind.setOff();
    }

    /**
     * Emulates the RPG concept of a Program message. //TODO Code this!
     * 
     * @param messageId
     * @param message
     *            - will be passed on to the exception
     */
    public void addProgramMessage(final String message) {
    }

    /**
     * Cache specified instance. If instance is null, then remove cached object
     * with specified key.
     * 
     * @param clazz
     * @param instance
     */
    public void cacheRoutine(Class clazz, final Object instance) {
        if (clazz != null || instance != null) {
            if (clazz == null) {
                clazz = instance.getClass();
            }
            cacheRoutine(clazz.getName(), instance);
        }
    }

    /**
     * Cache Object specified
     * 
     * @param instance
     */
    public void cacheRoutine(final Object instance) {
        if (instance != null) {
            cacheRoutine(instance.getClass().getName(), instance);
        }
    }

    /**
     * Internally used to cach an instance with specified key. a SoftReference
     * will be create to avoid memory leaking.
     * 
     * @param <T>
     * @param routineId
     * @param instance
     */
    @SuppressWarnings("unchecked")
    private <T> void cacheRoutine(final String routineId, final T instance) {
        /* instance is null, then remove and finalize cached routine */
        if (instance == null) {
            try {
                Object routine = routines.remove(routineId);
                if (routine != null) {
                    FinalizingUtils.toFinalize(routine);
                }
            } catch (RuntimeException e) {
                addError(e);
            }
        } else {
            routines.put(routineId, new SoftReference<T>(instance));
        }
    }

    /**
     * Simulate function of cobol's CANCEL statement. Add by Max Wang (CSC) to
     * fix bug 133. 28 March 2008
     * 
     * @param toCancelClass
     */
    @SuppressWarnings("unchecked")
    public void cancel(final BaseData toCancelProgram) {
        String whatToCallName = toCancelProgram.toString().trim();
        Class<? extends COBOLConvCodeModel> toCancelProgramClass = null;
        try {
            toCancelProgramClass = (Class<? extends COBOLConvCodeModel>) getProgram(whatToCallName);
        } catch (RuntimeException e) {
            throw new ServerException(e, null, "Unable to load program '" + whatToCallName + "' by *AppLocator");
        }

        cacheRoutine(toCancelProgramClass, null);
    }

    /**
     * Simulate function of cobol's CANCEL statement. Add by Max Wang (CSC) to
     * fix bug 133. 28 March 2008
     * 
     * @param toCancelClass
     */
    public void cancel(final Class toCancelClass) {
        cacheRoutine(toCancelClass, null);
    }

    /**
     * Emulates the RPG CHGCURLIB command. The current library is represented by
     * the first element in the liabraryList.
     * 
     * @param currentLibraryName
     *            - the name to replace the current library name.
     */
    public void changeCurrrentLibrary(final Object ocurrentLibraryName) {
        String currentLibraryName = ocurrentLibraryName.toString().trim();
        if (libraryList.size() == 0) {
            libraryList.add(currentLibraryName.toString().trim());
        } else {
            libraryList.set(0, currentLibraryName.toString().trim());
        }
    }

    /**
     * Emulates the RPG CHGLIBL command.
     * 
     * @param newlibs
     *            - replaces all entries in the library list after entry 0.
     */
    public void changeLibraryList(final String newlibs) {
        changeLibraryList(newlibs, null);
    }

    /**
     * Emulates the RPG CHGLIBL command.
     * 
     * @param newlibs
     *            - replaces all entries in the library list after entry 0.
     * @param changedCurLib
     *            - replaces entry 0. *CRTDFT can be supplied, in which case
     *            QGPL is used.
     */
    public void changeLibraryList(final String newlibs, final String changedCurLib) {
        String[] list = newlibs.trim().split(" ++");
        changeLibraryList(list, changedCurLib);
    }

    /**
     * Emulates the RPG CHGLIBL command.
     * 
     * @param newlibs
     *            - replaces all entries in the library list after entry 0.
     * @param changedCurLib
     *            - replaces entry 0. *CRTDFT can be supplied, in which case
     *            QGPL is used.
     */
    public void changeLibraryList(String[] list, final String changedCurLib) {
        String currentElt0 = libraryList.size() == 0 ? "QGPL" : libraryList.get(0);
        if (list.length == 1) {
            if (list[0].equalsIgnoreCase(SAME)) {
                list = libraryList.toArray();
            } else if (list[0].equalsIgnoreCase(NONE)) {
                list = new String[0];
            }
        }
        libraryList.clear();
        if (changedCurLib != null) {
            if (changedCurLib.equalsIgnoreCase(CRTDFT)) {
                libraryList.add("QGPL");
            } else if (changedCurLib.equalsIgnoreCase(SAME)) {
                libraryList.add(currentElt0);
            } else {
                libraryList.add(changedCurLib);
            }
        } else {
            libraryList.add(currentElt0);
        }
        for (int i = 0; i < list.length; i++) {
            addLibraryListEntry(list[i], LAST);
        }
    }

    /**
     * Clears all indicators.
     */
    public void clearAllInds() {
        for (int i = 0; i < IndicArea.length; i++) {
            IndicArea[i].set(false);
        }
        for (int i = 0; i < RPGOA.length; i++) {
            RPGOA[i].set(false);
        }
    }

    @Override
    public void clearData() {
        super.clearData();

        if (overriddenTables != null) {
            overriddenTables.clear();
            overriddenTables = null;
        }

        if (fieldChangeIndicators != null) {
            fieldChangeIndicators.clear();
            overriddenTables = null;
        }

        if (displayedMessages != null) {
            displayedMessages.clear();
            displayedMessages = null;
        }

        if (monitoredMessages != null) {
            monitoredMessages.clear();
            monitoredMessages = null;
        }

        if (userProfile != null) {
            userProfile.clear();
            userProfile = null;
        }

        if (printerFiles != null) {
            printerFiles.clear();
            printerFiles = null;
        }

        if (routines != null) {
            routines.clear();
        }

        if (arrays != null) {
            arrays.clear();
            arrays = null;
        }
    }

    public void clearHibernateSession() {
        if (this.hibernateSession != null) {
            this.hibernateSession.flush();
            this.hibernateSession.clear();
        }
    }

    public void clearRoutines() {
        routines.clear();
    }

    /**
     * implementation of COMMIT operation. to commit updates from the last
     * execution of CMMIT or ROLLBACK; simply, call connection.commit(); Max W
     */
    @Override
    public void commit() throws ExtMsgException {
        final Connection conn = getTxConnection();
        // txConnection is null, means the transaction contorl has not been
        // started yet
        if (txConnection == null) {
            addExtMessage("CPF8350", "Commitment definition not found.");
        }
        // commit changes
        try {
            // diff policy with life
            // clear cached alno number
            COBOLAppVars appVars = (COBOLAppVars) AppVars.getInstance();
            List<FixedLengthStringData> list = appVars.alocnoMap.get(appVars.key);
            if (list != null) {
                list.clear();
            }
            // if(!txConnection.isClosed())
            // txConnection.commit();
            if (this.getHibernateSession() != null) {
                this.getHibernateSession().getTransaction().commit();
                this.getHibernateSession().clear();
                this.getHibernateSession().getTransaction().begin();
            }
            // end diff policy with life
        } catch (Exception e) {
            addDiagnostic(e.getLocalizedMessage());
            addExtMessage("CPF8363", "Commit operation failed.");
        }
    }

    /*
     * This method is <b>NOT</b> used for <b>transaction control</b> at all.
     * It's a convenient method to commit a connection. all application code
     * should not commit connection straightly. this method check if specified
     * connection is used for transaction control; if true, then do nothing;
     * otherwise commit the connection.
     */
    @Override
    public void commit(Connection conn) throws SQLException {
        if (conn == null || conn == txConnection) {
            return;
        } else {
            super.commit(conn);
        }
    }

    // TODO reconcile with the method below!
    /**
     * Implements the RPG CL command DLTOVR. This version will not remove screen
     * overrides.
     * 
     * @see com.quipoz.framework.util.ISeriesScreenVarsSharedStore for detailed
     *      doco.
     * @param name
     *            - name of a resource being de-overriden. Note, "*ALL" can be
     *            specified. In this case, all overrides are removed.
     * @deprecated - was added for generated code to become "compileable". MLo.
     */
    @Deprecated
    public void deleteOverride(final String name) {
        if ("*ALL".equals(name)) {
            getOverriddenTables().clear();
        } else {
            getOverriddenTables().remove(name);
        }
    }

    /**
     * Implements the RPG CL command DLTOVR.
     * 
     * @see com.quipoz.framework.util.ISeriesScreenVarsSharedStore for detailed
     *      doco.
     * @param name
     *            - name of a resource being de-overriden. Note, "*ALL" can be
     *            specified.
     * @param requestor
     *            - id of caller program (this/ROUTINE), same as for
     *            overrideDisplayScreen() method below In this case, all
     *            overrides are removed.
     */
    public void deleteOverride(final String name, final Object requestor) {
        ISeriesScreenVarsSharedStore svss = getSharedScreens();
        svss.release(requestor, name);
        new PrintManager().deleteOverride(name);
        if ("*ALL".equals(name)) {
            getOverriddenTables().clear();
        } else {
            getOverriddenTables().remove(name);
        }
        // new PrinterManager().deleteOverride(name);
    }

    public void deleteOverride(final String[] name, final Object requestor) {
        for (int i = name.length; i-- > 0;) {
            deleteOverride(name[i], requestor);
        }
    }

    /**
     * An convenient method for deriving progam calss by specified program name.
     * The content is moved from getProgam. This method will be used by
     * ISeriesObjectUtils.checkProgramExistence(). Not much work has been done
     * here. It might need to be updated in accordance to demands.
     * 
     * @param progname
     * @return
     * @throws ExtMsgException
     */
    public Class deriveProgramClass(final Object progname) throws ExtMsgException {
        if (progname == null) {
            return null;
        }
        String newProg = progname.toString().trim();
        // If not a full class name, then use default class path.
        if (newProg.indexOf(".") < 0) {
            newProg = getAppConfig().subPath + ".procedures." + progname.toString().trim();
        }
        try {
            return Class.forName(newProg);
        } catch (Exception e) {
            addError("Unable to load program '" + newProg + "'");
            addExtMessage("CPF9801", e);
            return null;
        }
    }

    @Override
    public void doOnException() {
        /*
         * to rollback pending modifications
         */
        if (isCommitControlStarted()) {
            try {
                // txConnection.rollback();
                if (this.getHibernateSession() != null) {
                    this.getHibernateSession().getTransaction().rollback();
                }
            } catch (Exception e) {
                // suppress the exception, as there is no way to recovery.
                LOGGER.error("SQLException:\n", e);
            }
        }
    }

    /**
     * implementation of ENDCMTCTL at the moment, it's difficult to detect if
     * there are existing updates holding by the connection; An alternative way
     * is to commit it and then close the connection. txConnection is set to be
     * null evantually; Max W
     */
    @Override
    public void endCommitControl() throws ExtMsgException {

        // txConnection is null, means the transaction contorl has not been
        // started yet
        if (txConnection == null) {
            addExtMessage("CPF8350", "Commitment definition not found.");
        }

        // clean up the temporary table involved in the transaction TODO

        // close connection
        try {
            // as there is no way to detect the pending updates at the moment,
            // then simply commit changes before end
            // clean up connection and assign it as null
            // txConnection.commit();
            txConnection.close();
            txConnection = null;
            if (this.getHibernateSession() != null) {
                this.getHibernateSession().close();
                this.setHibernateSession(null);
            }
        } catch (SQLException e) {
            String msg = "Errors occurred while ending commitment control.";
            addExtMessage("CPF835B", msg);
            LOGGER.error(msg + " Reason:\n", e);
        }
        return;
    }

    @Override
    public boolean executeAndCommitForTempTable(String sql, boolean isCommit) throws SQLException {
        Connection conn = getTempDBConnection(APPVARS);
        Statement stmt = conn.createStatement();
        try {
            boolean ret = executeStatement(stmt, sql);
            if (isCommit) {
                conn.commit();
            }

            int indx = sql.indexOf("#");
            if (indx > 0) {
                String[] arr = sql.substring((indx + 1), sql.length() - indx).trim().split("\\s+");
                if ((arr != null) && (arr.length > 1)) {
                    temporaryTables.add(arr[0]);
                }
            }
            return ret;
        } catch (SQLException se) {
            throw se;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e1) {
                }
            }
            freeDBConnectionIgnoreErr(conn);
        }
    }

    /**
     * Execute a Select that is assumed to be a simple select for update and
     * return a ResultSet after having fetched the first row. This code is
     * separated out for convenience in tracing so that the timing and SQL codes
     * are reported correctly. The reason is, the error if any occur on the
     * fetch not on the SQL execution.
     * 
     * @param fromWhere
     *            for diagnostic purposes, from where this SQL was executed.
     *            E.g. name of the program.
     * @param aPS
     *            the passed prepared statement.
     * @param aSql
     *            the current SQL statement.
     * @return ResultSet as a result of execution.
     * @throws SQLException
     *             on error.
     */
    @Override
    public ResultSet executeLockQuery(final String fromWhere, final PreparedStatement aPS, final String aSql)
            throws SQLException {
        ResultSet rs = super.executeLockQuery(fromWhere, aPS, aSql);
        setNumberOfRowsProcessed(rs.getFetchSize());
        return rs;
    }

    /**
     * Execute a Select and return a ResultSet.
     * 
     * @param fromWhere
     *            for diagnostic purposes, from where this SQL was executed.
     *            E.g. name of the program.
     * @param aPS
     *            the passed prepared statement.
     * @param aSql
     *            the current SQL statement.
     * @return ResultSet
     * @throws SQLException
     *             on error.
     */
    @Override
    public ResultSet executeQuery(final String fromWhere, final PreparedStatement aPS, final String aSql)
            throws SQLException {
        ResultSet rs = super.executeQuery(fromWhere, aPS, aSql);
        setNumberOfRowsProcessed(rs.getFetchSize());
        return rs;
    }

    public int executeSingleRowQuery(final String sqlstmt, final Object[] sqlparams, final Object[] sqlinto)
            throws SQLException {
        return executeSingleRowQuery(getClass().getName(), sqlstmt, sqlparams, sqlinto);

    }

    /**
     * Execute a non-Select SQL statement.
     * 
     * @param fromWhere
     *            for diagnostic purposes, from where this SQL was executed.
     *            E.g. name of the program.
     * @param aPS
     *            the passed prepared statement. - previously prepared
     *            statement.
     * @param aSql
     *            the current SQL statement.
     * @return int - number of rows affected.
     * @throws SQLException
     *             on error.
     */
    @Override
    public int executeUpdate(final String fromWhere, final PreparedStatement aPS, final String aSql)
            throws SQLException {
        int rows = super.executeUpdate(fromWhere, aPS, aSql);
        setNumberOfRowsProcessed(rows);
        return rows;
    }

    /**
     * Format the possible schemas as a String separated by line feeds.
     * 
     * @param library
     * @return String of possibles
     */
    public String formatPossibleSchemas(final String library) {

        String[] possibleSchemas = getPossibleSchemas(library);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < possibleSchemas.length; i++) {
            sb.append('\n');
            sb.append(possibleSchemas[i]);
        }
        return sb.toString();
    }

    @Override
    public void freeDBConnectionIgnoreErr(final Connection conn, final Statement aPS, final ResultSet aResultSet) {
        setSqlErrorCode(null);
        super.freeDBConnectionIgnoreErr(conn, aPS, aResultSet);
    }

    /**
     * Routine available to free any resources eg caches. Runs finalise on all
     * objects in the routines cache.
     */
    @Override
    public void freeResources(final String... exceptFullClassNames) {
        super.freeResources();

        /* Finalize all cached programs except specified ones */
        List<String> exceptedClass;
        if (exceptFullClassNames != null && exceptFullClassNames.length > 0) {
            exceptedClass = Arrays.asList(exceptFullClassNames);
        } else {
            exceptedClass = new ArrayList<String>(0);
        }

        Object[] keys = routines.keySet().toArray();
        Object key = null;
        COBOLConvCodeModel prog = null;
        for (int i = 0; i < keys.length; i++) {
            key = keys[i];
            if (exceptedClass.contains(key)) {
                continue;
            }
            Object o = routines.get(key);
            if (o instanceof COBOLConvCodeModel) {
                prog = (COBOLConvCodeModel) o;
            }
            routines.remove(key);
        }
    }

    /**
     * Get Object cached in appVars.routines.
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getCachedRoutine(final Class<T> clazz) {
        if (clazz != null) {
            Object routine = routines.get(clazz.getName());
            if (routine != null) {
                if (routine instanceof Reference<?>) {
                    routine = ((Reference) routine).get();
                    if (routine == null) {
                        routines.remove(clazz.getName());
                    }
                }
                if (routine == null) {
                    return null;
                } else if (clazz.isAssignableFrom(routine.getClass())) {
                    return (T) routine;
                } else {
                    addDiagnostic("Routine cached is not in type of expected - " + clazz.getName()
                            + " is expected, but actual routine is " + routine.getClass().getName());
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Supports the RPG concept of a positioned array. An array has a conceptual
     * position, which is set only by the lookup function. Subsequent references
     * to the array without any element qualification mean the element that was
     * found.
     * 
     * @param anArray
     * @return BaseData - an element from the array. If no lookup has been done,
     *         returns element[1].
     */
    public int getCurrentIndexNumberOf(final Object anArray) {
        int ix = 1;
        IntegerData ind = (IntegerData) arrays.get(anArray + "");
        if (ind != null) {
            ix = ind.toInt();
        }
        return ix;
    }

    /**
     * Supports the RPG concept of a positioned array. An array has a conceptual
     * position, which is set only by the lookup function. Subsequent references
     * to the array without any element qualification mean the element that was
     * found.
     * 
     * @param anArray
     * @return BaseData - an element from the array. If no lookup has been done,
     *         returns element[1].
     */
    public Object getCurrentIndexOf(final Object[] anArray) {
        int ix = 1;
        IntegerData ind = (IntegerData) arrays.get(anArray + "");
        if (ind != null) {
            ix = ind.toInt();
        }
        return anArray[ix];
    }

    /**
     * Supports the RPG concept of a positioned array. An array has a conceptual
     * position, which may be sey via an occur clause. Subsequent references to
     * the array without any element qualification mean the element referenced
     * by the passed variable.
     * 
     * @param anArray
     * @return IntegerData - the current index object. One will be created if it
     *         doesn't exist.
     */
    public IntegerData getCurrentLocatorOf(final Object anArray) {
        IntegerData ind = (IntegerData) arrays.get(anArray + "");
        if (ind == null) {
            ind = new IntegerData(1);
            arrays.put(anArray + "", ind);
        }
        return ind;
    }

    public Connection getDBConnection() throws SQLException {
        return getDBConnection("n/a");
    }

    /**
     * Gets a message by looking it up. No library, so the first such message is
     * used.
     * 
     * @param msgid
     *            - Message number
     * @return The message
     */
    public String getDBMessage(final Object msgid) {
        String result = getDBMessage(msgid, null, nullind);
        if (result == null) {
            result = "Message '" + msgid + "' was raised, but not found in the message tables.";
        }
        return result;
    }

    /**
     * Gets a message by looking it up and substituting in the supplied data.
     * Example:
     * <ol>
     * <li>Message is MSG123 which contains "Error - &1 is not a valid &2 for &3
     * &4."
     * <li>T_MESSAGE_VAR contains four entries for MSG123
     * <table>
     * <tr>
     * <th>No</th>
     * <th>Len</th>
     * <th>Decs</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>7</td>
     * <td>2</td>
     * <td>DEC</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>6</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>2</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>7</td>
     * <td>2</td>
     * <td>CHAR</td>
     * </tr>
     * </table>
     * <li>"subst" contains "xxxxamountaninvoice" where xxxx actually contains a
     * decimal packed number, hex=0x"3141592f".
     * </ol>
     * The message will be "Error - 31415.92 is not a valid amount for an
     * invoice."
     * 
     * @param msgid
     *            - Message number
     * @param subst
     *            - Parameters to substitute. The encoding is contained in table
     *            T_MESSAGE_VAR.
     * @return - The formatted message
     */
    public String getDBMessage(final Object msgid, final Object subst) {
        String result = getDBMessage(msgid, null, nullind);
        if (result == null) {
            result = "Message '" + msgid + "' was raised, but not found in the message tables.";
        } else if (result.trim().length() > 0) {
            result = getDBSubstitutions(result, msgid, null, subst, nullind);
        }
        return result;
    }

    /**
     * Gets a message by looking it up in the nominated message library and
     * substituting in the supplied data. Example:
     * <ol>
     * <li>Message is MSG123 which contains "Error - &1 is not a valid &2 for &3
     * &4."
     * <li>T_MESSAGE_VAR contains four entries for MSG123
     * <table>
     * <tr>
     * <th>No</th>
     * <th>Len</th>
     * <th>Decs</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>7</td>
     * <td>2</td>
     * <td>DEC</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>6</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>2</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>7</td>
     * <td>2</td>
     * <td>CHAR</td>
     * </tr>
     * </table>
     * <li>"subst" contains "xxxxamountaninvoice" where xxxx actually contains a
     * decimal packed number, hex=0x"3141592f".
     * </ol>
     * The message will be "Error - 31415.92 is not a valid amount for an
     * invoice."
     * 
     * @param msgid
     *            - Message number
     * @param msgfile
     *            - Message library. May be null, in which case the first
     *            message of the given id on the table will be used. This may
     *            not be the onle you want!
     * @param ind
     *            - Indicator to set on if the message is not found
     * @return - The formatted message
     */
    public String getDBMessage(final Object msgid, final Object msgfile, final Indicator ind) {

        if (msgid == null || msgid.toString() == null || msgid.toString().trim().length() == 0) {
            return "";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        String result = null;

        /*String sql = "SELECT MESSAGE_LONG_DS FROM " + getAppConfig().getAppSchema() + '.'
                + "APP_MESSAGE WHERE MESSAGE_ID = ?";
        if (msgfile != null) {
            sql += " AND MESSAGE_FILE = ?";
        }*/
        String sql = "SELECT ERORDESC FROM " +  getAppConfig().getAppSchema() + '.'
        	+ "ERORPF WHERE ERORLANG=? AND EROREROR = ?";
			if (msgfile != null) {
			    sql += " AND ERORFILE = ?";
			}
        try {
            /*
             * Max W as it seems not supposed to be involved in transaction
             * control but not sure if it access temporary tables then replace
             * getDBConnection with getTempDBConnection
             */
            conn = getTempDBConnection(ROUTINE);
            ps = prepareStatement(conn, sql);

            // 2008-04-02 modified by wayne.yang.
            // msgid must have a length of 8,else the query will fail.
            // The same happens in msgfile,which requires a length of 10.
            FixedLengthStringData flsd = new FixedLengthStringData(8);
            flsd.set(msgid);
            setDBString(ps, 1, flsd.substring(0,1).toString());
            setDBString(ps, 2, flsd.substring(1,7).toString());
            if (msgfile != null) {
            	flsd = new FixedLengthStringData(6);
                flsd.set(msgfile);
                setDBString(ps, 3, flsd.toString());
            }
            rs = executeQuery(ps);
            if (fetchNext(rs)) {
                result = getDBString(rs, 1);
            }
        } catch (SQLException ex1) {
            LOGGER.error("SQLException:\n", ex1);
            if (ind == null) {
                result = "Error message '" + msgid + "' was raised but resulted in SQL error " + ex1;
            } else {
                ind.setOn();
            }
        } finally {
            freeDBConnectionIgnoreErr(conn, ps, rs);
        }

        // if (result == null) {
        // result = "Message '" + msgid
        // + "' was raised, but not found in the message tables.";
        // }

        return result;
    }

    /**
     * Gets a message by looking it up in the nominated message library and
     * substituting in the supplied data. Example:
     * <ol>
     * <li>Message is MSG123 which contains "Error - &1 is not a valid &2 for &3
     * &4."
     * <li>T_MESSAGE_VAR contains four entries for MSG123
     * <table>
     * <tr>
     * <th>No</th>
     * <th>Len</th>
     * <th>Decs</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>7</td>
     * <td>2</td>
     * <td>DEC</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>6</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>2</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>7</td>
     * <td>2</td>
     * <td>CHAR</td>
     * </tr>
     * </table>
     * <li>"subst" contains "xxxxamountaninvoice" where xxxx actually contains a
     * decimal packed number, hex=0x"3141592f".
     * </ol>
     * The message will be "Error - 31415.92 is not a valid amount for an
     * invoice."
     * 
     * @param msgid
     *            - Message number
     * @param msgfile
     *            - Message library
     * @param subst
     *            - Parameters to substitute. The encoding is contained in table
     *            T_MESSAGE_VAR.
     * @return - The formatted message
     */
    public String getDBMessage(final Object msgid, final Object msgfile, final Object subst) {
        String result = getDBMessage(msgid, msgfile, nullind);
        if (result == null) {
            result = "Message '" + msgid + "' was raised, but not found in the message tables.";
        } else if (result.trim().length() > 0) {
            result = getDBSubstitutions(result, msgid, msgfile, subst, nullind);
        }
        return result;
    }

    /**
     * Gets a message by looking it up in the nominated message library and
     * substituting in the supplied data. Example:
     * <ol>
     * <li>Message is MSG123 which contains "Error - &1 is not a valid &2 for &3
     * &4."
     * <li>T_MESSAGE_VAR contains four entries for MSG123
     * <table>
     * <tr>
     * <th>No</th>
     * <th>Len</th>
     * <th>Decs</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>7</td>
     * <td>2</td>
     * <td>DEC</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>6</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>2</td>
     * <td>0</td>
     * <td>CHAR</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>7</td>
     * <td>2</td>
     * <td>CHAR</td>
     * </tr>
     * </table>
     * <li>"subst" contains "xxxxamountaninvoice" where xxxx actually contains a
     * decimal packed number, hex=0x"3141592f".
     * </ol>
     * The message will be "Error - 31415.92 is not a valid amount for an
     * invoice."
     * 
     * @param msgid
     *            - Message number
     * @param msgfile
     *            - Message library
     * @param subst
     *            - Parameters to substitute. The encoding is contained in table
     *            T_MESSAGE_VAR.
     * @param ind
     *            - Indicator to be set if the message is not found.
     * @return - The formatted message
     */
    public String getDBMessage(final Object msgid, final Object msgfile, final Object subst, final Indicator ind) {
        String result = getDBMessage(msgid, msgfile, ind);
        if (result == null) {
            result = getDBMessage(msgid.toString().substring(0, 2) + "T0000 ", msgfile, ind);
        }
        if (result == null) {
            result = "Message '" + msgid + "' was raised, but not found in the message tables.";
        } else if (result.trim().length() > 0) {
            result = getDBSubstitutions(result, msgid, msgfile, subst, ind);
        }
        return result;
    }

    /**
     * Looks up substitution parameters for a message
     * 
     * @param result
     *            - Message passed in.
     * @param msgid
     *            - Message Id
     * @param msgfile
     *            - Message Library
     * @param substitutions
     *            - Data to substitute in
     * @param ind
     *            - Indicator to set on in case of not found error
     * @return
     */
    public String getDBSubstitutions(String result, final Object msgid, final Object msgfile,
            final Object substitutions, final Indicator ind) {

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ArrayList al = new ArrayList();

        /* Put a dummy entry in position zero to make numbers align. */
        al.add("");

        String schema = getAppConfig().getFwSchema();

        String sql = "SELECT MESSAGE_VAR_NO, MESSAGE_VAR_LN, MESSAGE_DEC_PLACE, MESSAGE_VAR_TY FROM " + schema + '.'
                + "APP_MESSAGE_VAR WHERE MESSAGE_ID = ?";
        if (msgfile != null) {
            sql += " AND MESSAGE_FILE = ?";
        }

        sql += " ORDER BY MESSAGE_VAR_NO";

        try {
            /*
             * Max W as it seems not supposed to be involved in transaction
             * control then replace it with getTempDBConnection
             */
            conn = getTempDBConnection(ROUTINE);
            ps = prepareStatement(conn, sql);

            // 2008-04-02 modified by wayne.yang.
            // msgid must have a length of 8,else the query will fail.
            // The same happens in msgfile,which requires a length of 10.
            FixedLengthStringData flsd = new FixedLengthStringData(8);
            flsd.set(msgid);
            setDBString(ps, 1, flsd.toString());
            if (msgfile != null) {
                flsd = new FixedLengthStringData(10);
                flsd.set(msgfile);
                setDBString(ps, 2, flsd.toString());
            }
            rs = executeQuery(ps);
            while (fetchNext(rs)) {
                al.add(new String[] { getDBString(rs, 2), getDBString(rs, 3), getDBString(rs, 4) });
            }
        } catch (SQLException ex1) {
            LOGGER.error("SQLException:\n", ex1);
            if (ind == null) {
                result = "Error message '" + msgid + "' was raised but resulted in SQL error " + ex1;
            } else {
                ind.setOn();
            }
        } finally {
            freeDBConnectionIgnoreErr(conn, ps, rs);
        }

        String type = "";
        int len = 0;
        int bytes = 0;
        int decs = 0;
        int off = 0;
        String[] entry = null;

        if (substitutions == null) {
            type = "";
            len = 78;
        } else {
            type = substitutions.toString();
            len = Math.max(78, type.length());
        }
        FixedLengthStringData subst = new FixedLengthStringData(len, type);

        // for (int i=al.size()-1; i>0; i--) {
        // Above line is commented for issue 1906.
        // Reason - Changing the order also reverses the variables in the
        // message
        // Thus a message which should read "File 'X' is not found in directory
        // 'D'" comes out as
        // "File 'D' is not found in directory 'X'"
        for (int i = 1; i < al.size(); i++) {
            entry = (String[]) al.get(i);
            len = QPUtilities.cInt(entry[0]);
            decs = QPUtilities.cInt(entry[1]);
            type = entry[2].trim();
            if (len == 0) {
                continue;
            }
            if (type.equals("*QTDCHAR")) {
                bytes = len;
            } else if (type.equals("*CHAR")) {
                bytes = len;
            } else if (type.equals("*HEX")) {
                bytes = len;
            } else if (type.equals("*DEC")) {
                bytes = (len + 1) / 2;
            } else if (type.equals("*BIN")) {
                bytes = len;
            } else if (type.equals("*UBIN")) {
                bytes = len;
            } else if (type.equals("*CCHAR")) {
                bytes = len;
            } else if (type.equals("*DTS")) {
                bytes = 8;
            } else if (type.equals("*SPP")) {
                bytes = 16;
            } else if (type.equals("*SYP")) {
                bytes = 16;
            } else if (type.equals("*ITV")) {
                bytes = 8;
            }

            if (bytes + off > subst.toString().length()) {
                break;
            }

            if (type.equals("*QTDCHAR")) {
                FixedLengthStringData fsd = new FixedLengthStringData(len).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, "'" + fsd.toString() + "' ");
            } else if (type.equals("*CHAR")) {
                FixedLengthStringData fsd = new FixedLengthStringData(len).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, fsd.toString().trim() );
            } else if (type.equals("*HEX")) {
                FixedLengthStringData fsd = new FixedLengthStringData(len).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, fsd.toHexString() + ' ');
            } else if (type.equals("*DEC")) {
                /*
                 * Save data - overdefine of pd zaps the parent. Possibly
                 * incorrect.
                 */
                String now = subst.toString();
                PackedDecimalData pd = new PackedDecimalData(len, decs)
                        .isAPartOf(subst, off, FieldType.NON_DECLARATION);
                subst.set(now);
                off += pd.getBytes();
                result = QPUtilities.replaceSubstring(result, "&" + i, pd.toString() + ' ');
            } else if (type.equals("*BIN")) {
                BinaryData bd = new BinaryData(len * 2).isAPartOf(subst, off, FieldType.NON_DECLARATION);
                off += bd.getBytes();
                result = QPUtilities.replaceSubstring(result, "&" + i, bd.toString() + ' ');
            } else if (type.equals("*UBIN")) {
                BinaryData bd = new BinaryData(len * 2).isAPartOf(subst, off, FieldType.NON_DECLARATION);
                off += bd.getBytes();
                result = QPUtilities.replaceSubstring(result, "&" + i, bd.toString() + ' ');
            } else if (type.equals("*CCHAR")) {
                BinaryData bd = new BinaryData(4).isAPartOf(subst, off, FieldType.NON_DECLARATION);
                off += 2;
                FixedLengthStringData fsd = new FixedLengthStringData(bd.toInt()).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += bd.toInt();
                result = QPUtilities.replaceSubstring(result, "&" + i, fsd.toString().trim() + ' ');
            } else if (type.equals("*DTS")) {
                FixedLengthStringData fsd = new FixedLengthStringData(len).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, fsd.toHexString() + ' ');
            } else if (type.equals("*SPP")) {
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, " *SPP Object not catered for");
            } else if (type.equals("*SYP")) {
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, " *SYP Object not catered for");
            } else if (type.equals("*ITV")) {
                FixedLengthStringData fsd = new FixedLengthStringData(len).isAPartOf(subst, off,
                        FieldType.NON_DECLARATION);
                off += len;
                result = QPUtilities.replaceSubstring(result, "&" + i, fsd.toHexString() + ' ');
            }
        }
        return (result == null ? "" : result.trim());
    }

    public Hashtable getDisplayedMessages() {
        return displayedMessages;
    }

    protected Hashtable<String, Object> getFieldChangeIndicators() {
        if (fieldChangeIndicators == null) {
            fieldChangeIndicators = new Hashtable<String, Object>();
        }
        return fieldChangeIndicators;
    }

    public Session getHibernateSession() {
        return this.hibernateSession;
    }

    public boolean getInd(BaseData ind) {
        return getInd(ind.toInt());
    }

    public boolean getInd(final int ind) {
        if (ind < 0 || ind > 99) {
            throw new RuntimeException("Indicator exception " + ind);
        }
        return IndicArea[ind].isOn();
    }

    /**
     * Returns the JobInfo object primarily used for storing job information
     * 
     * @return jobInfo
     */
    public JobInfo getJobInfo() {
        return jobInfo;
    }

    /**
     * Obtain access to the libary list. You can replace the contents, but you
     * cannot replace the object.
     * 
     * @return The library list.
     */
    public StringArrayList getLibraryList() {
        return libraryList;
    }

    /**
     * Gets the metadata for this table ie the column types of all columns. The
     * information is held in a static cache for performance.
     * 
     * @param dam
     *            - name of the dam for which info is required.
     * @param tableName
     *            - name of the table concerned.
     * @param columns
     *            - list of columns required.
     * @return array of column types as per the result set.
     */
    public int[] getMetaData(final String dam, String tableName, final String columns) {

        int[] colTypes = (int[]) tableMetaData.get(dam);

        if (colTypes != null) {
            return colTypes;
        }

        String tableName2 = getTableName(tableName);

        if (tableName2 == null) {
            throw new RuntimeException("Table '" + tableName + "' not found in any available schema. Schemas are "
                    + formatPossibleSchemas(LIBL));
        }

        tableName = tableName2;

        String sql = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getTempDBConnection(ROUTINE + " setMetaData");
            sql = "SELECT " + columns + " FROM " + tableName + " FETCH FIRST 1 ROWS ONLY WITH UR ";
            ps = prepareStatement(conn, sql);
            rs = executeQuery(ps);
            ResultSetMetaData md = rs.getMetaData();
            colTypes = new int[md.getColumnCount()];
            for (int i = 0; i < colTypes.length && i < md.getColumnCount(); i++) {
                colTypes[i] = md.getColumnType(i + 1);
            }
            tableMetaData.put(dam, colTypes);
            return colTypes;
        } catch (SQLException ex1) {
            LOGGER.error("Error accessing table. Reason:\n", ex1);
        } finally {
            freeDBConnectionIgnoreErr(null, ps, rs);
        }

        /*
         * Commonest cause for problems after simple SQL failure or table not
         * there is that the database definition does not match the DAM column
         * definition. This diagnostic code tries to find out why.
         */
        try {
            String[] cola = columns.trim().toUpperCase().split("[ ,]++");
            sql = "SELECT * FROM " + tableName + " WITH UR";
            conn = getTempDBConnection(ROUTINE + " setMetaData");
            ps = prepareStatement(conn, sql);
            rs = executeQuery(ps);
            ResultSetMetaData md = rs.getMetaData();
            String[] colb = new String[md.getColumnCount()];
            for (int i = 0; i < md.getColumnCount(); i++) {
                colb[i] = md.getColumnName(i + 1).toUpperCase();
            }
            for (int i = 0; i < cola.length; i++) {
                if (cola[i] == null) {
                    continue;
                }
                for (int j = 0; j < colb.length; j++) {
                    if (colb[j] == null) {
                        continue;
                    }
                    if (cola[i].equals(colb[j])) {
                        cola[i] = null;
                        colb[j] = null;
                        break;
                    }
                }
            }
            String s = "There is a mismatch between the database table '" + tableName + "' and the DAM '" + dam + "'.";
            String t = null;
            for (int i = 0; i < cola.length; i++) {
                if (cola[i] != null) {
                    if (t == null) {
                        t = "\nColumns in the DAM but not in the table: " + cola[i];
                    } else {
                        t += ", " + cola[i];
                    }
                }
            }
            String u = null;
            for (int j = 0; j < colb.length; j++) {
                if (colb[j] != null) {
                    if (u == null) {
                        u = "\nColumns in the table but not in the DAM: " + colb[j];
                    } else {
                        u += ", " + colb[j];
                    }
                }
            }
            if (t != null) {
                s += t;
            }
            if (u != null) {
                s += u;
            }
            throw new RuntimeException(s);
        } catch (SQLException ex1) {
            LOGGER.error("getMetaData(String, String, String)", ex1);
        } finally {
            freeDBConnectionIgnoreErr(conn, ps, rs);
        }
        return colTypes;
    }

    /**
     * Obtain access to the monitored message list. You can replace the
     * contents, but you cannot replace the object.
     * 
     * @return The library list.
     */
    public Hashtable getMonitoredMessages() {
        return monitoredMessages;
    }

    /**
     * abstract method definition. when start commit control, a new database
     * connection has to be created. The actual implementation is carried out in
     * its subclass - SMARTAppVars, but the signature is defined here. Max W
     * 
     * @return
     * @throws SQLException
     */
    protected Connection getNewDBConnection() throws SQLException {
        throw new UnsupportedOperationException("the method should be overriden in its subclass.");
    }

    /**
     * Returns the number of rows processed in last sql statement.
     * 
     * @return int - number of rows processed
     */
    public int getNumberOfRowsProcessed() {
        return numberOfRowsProcessed;
    }

    /**
     * Return all the overrides. This is intended for use in RPGConvCodeModel
     * only, to save the contents when a program gets called, and restore it
     * afterwards.
     * 
     * @return overriddenTables
     */
    public Hashtable getOverriddenTables() {
        return overriddenTables;
    }

    /**
     * retrieves (optional) overriden display screen.
     * 
     * @see com.quipoz.framework.util.ISeriesScreenVarsSharedStore for detailed
     *      doco.
     * @param name
     *            - name of a resource, being shared by coller program.
     */
    public Object getOverrideDisplayScreen(final String name, final Object defaultScreenModel) {
        ISeriesScreenVarsSharedStore svss = getSharedScreens();
        Object sm = svss.find(name);
        return sm == null ? defaultScreenModel : sm;
    }

    private String getOverridenKey(String tableName) {
        if (tableName == null) {
            return null;
        }
        tableName = tableName.trim();
        if (tableName.endsWith(DAM_SUFFIX)) {
            return tableName;
        } else {
            StringBuilder sb = new StringBuilder(tableName.length() + DAM_SUFFIX.length());
            sb.append(tableName.toLowerCase()).append(DAM_SUFFIX);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        }
    }

    /**
     * For a given filename returns the current overrides. Note that the
     * protocol expects that the file name will end in "TableDam", eg
     * Ii110TableDam.
     * 
     * @param fileName
     * @return An array Object
     */
    public Object[] getOverrides(final String fileName) {
        if (overriddenTables != null) {
            return (Object[]) overriddenTables.get(fileName);
        } else {
            return null;
        }
    }

    private Object[] getOverridesWithoutSuffix(final String tableName) {

        if (overriddenTables == null || overriddenTables.isEmpty()) {
            return null;
        }
        String[] splitted = tableName.split("[\\./]");
        String table;
        if (splitted.length == 1) {
            table = splitted[0];
        } else {
            table = splitted[1];
        }
        return getOverrides(getOverridenKey(table));
    }

    /**
     * Get the list of possible schemas. This is either
     * <ol>
     * <li>The current library list
     * <li>The passed library name
     * </ol>
     * 
     * @param library
     * @return Array of possibles
     */
    public String[] getPossibleSchemas(String library) {

        /* IfLIBL, possible schemas = the current library list */
        if ((library = (library == null ? LIBL : library.trim().toUpperCase())).equals(LIBL)) {
            return getLibraryList().toArray();
        }
        /* or the current library */
        else if (library.equals(CURLIB)) {
            return new String[] { getLibraryList().toArray()[0] };
        }
        /* or the passed nominated schema */
        else {
            return new String[] { library };
        }
    }

    /**
     * Gets the specific metadata for this table of the presence of the table
     * member column. The information is held in the same static cache as
     * getMetaData for performance.
     * 
     * @param dam
     *            - name of the dam for which info is required.
     * @param tableName
     *            - name of the table concerned.
     * @return true=this is a member containing table
     */
    public boolean getPresenceOfMembers(final String dam, String tableName) {

        int[] colTypes = (int[]) tableMetaData.get(dam + "$members?");

        if (colTypes != null) {
            return colTypes[0] > 0;
        }

        String tableName2 = getTableName(tableName);

        if (tableName2 == null) {
            throw new RuntimeException("Table '" + tableName + "' not found in any available schema. Schemas are "
                    + formatPossibleSchemas(LIBL));
        }

        tableName = tableName2;

        String sql = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getTempDBConnection(ROUTINE + " setMetaData");
            sql = "SELECT * FROM " + tableName + " WITH UR";
            ps = prepareStatement(conn, sql);
            rs = executeQuery(ps);
            ResultSetMetaData md = rs.getMetaData();
            colTypes = new int[1];
            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (md.getColumnName(i).equals(FileCode.MEMBER_COLUMN)) {
                    colTypes[0] = 1;
                    break;
                }
            }
            tableMetaData.put(dam + "$members?", colTypes);
            return colTypes[0] > 0;
        } catch (SQLException ex1) {
            LOGGER.error("Error accessing table. Reason:\n", ex1);
        } finally {
            freeDBConnectionIgnoreErr(conn, ps, rs);
        }

        return false;
    }

    /**
     * Retrieves a specific printer file HashMap from the printerFiles HashMap
     * 
     * @param key
     *            the file name of the printer file to retrieve
     * @return value from HashMap
     */
    public HashMap getPrinterFile(final String key) {
        HashMap printerFile = (HashMap) printerFiles.get(key);
        return printerFile;
    }

    /**
     * Returns the HashMap containing all the printer file HashMaps
     * 
     * @return the hashmap containing the printer files
     */
    public HashMap getPrinterFilesMap() {
        return this.printerFiles;
    }

    /**
     * Class to resolve a program according to the application's class path.
     * This overrides the AppVars version and actually does something, though
     * not very much. You probably need to use the Application version of
     * AppVars.
     * 
     * @param progname
     * @return
     */
    @Override
    public Object getProgram(final Object progname) {
        addDiagnostic("Transfer-to '" + progname + "'");
        CallableProgram cprog = null;
        Class progClazz = deriveProgramClass(progname);
        try {
            cprog = (CallableProgram) progClazz.newInstance();
        } catch (Exception e) {
            logException(e);
            addError("Error " + e.getClass().getSimpleName() + " " + e.getMessage());
            throw new ServerException(e, null, "Unable to initialize program '" + progClazz.getName() + "'");
        }
        return cprog;
    }

    /**
     * @return the temporary file area connection
     */
    public Connection getQtempConnection() {
        return qtempConnection;
    }

    public Map<Object, Object> getRoutines() {
        // routines might be null after object has been deserialized
        if (routines == null) {
            routines = new HashMap<Object, Object>();
        }
        return Collections.unmodifiableMap(routines);
    }

    /**
     * Returns the Schema for a passed Object. This object is expected to be an
     * identifier. COBOLAppVars is normally extended by an Application subclass,
     * and that is where the code to do this is expected to be. The identifier
     * might be a number, a String or whatever. In this implementation, it just
     * returns the Object's toString method. If passed a null, it will return
     * the String "null".
     * 
     * @return String - the schema.
     */
    public String getSchema(final Object o) {
        return o + "";
    }

    public ISeriesScreenVarsSharedStore getSharedScreens() {
        return this.sharedScreenVars;
    }

    public Map<Object, Object> getSmartFileCodeSessionVariablesMap() {
        return smartFileCodeSessionVariablesMap;
    }

    /**
     * Get SQL error code, which might require mapping.
     * 
     * @return SQL error code
     */
    public int getSqlErrorCode() {
        return sqlCode;
    }

    public String getSqlMessage() {
        return sqlError == null ? "" : sqlError.getLocalizedMessage();
    }

    /**
     * Returns the fully qualified table name for a passed table according to
     * standard iSeries rules. This means search the current library list for
     * that table. If the table is already qualified, do not search for it.
     * 
     * @return schema + '.' + passed table name.
     */
    @Override
    public String getTableName(String tableName) {
        if (tableName.indexOf('/') >= 0) {
            tableName = tableName.replace('/', '.');
        }
        if (tableName.indexOf('.') >= 0) {
            return tableName;
        }
        return getTableName(LIBL, tableName);
    }

    /**
     * Returns the fully qualified table name for a passed table according to
     * standard iSeries rules. This means search the current library list for
     * that table.
     * 
     * @return schema + '.' + passed table name.
     */
    public String getTableName(final String library, final String tableName) {
        DatabaseCommon databaseCommon = DatabaseCommonFactory.getInstance(getAppConfig().getDatabaseType());
        return databaseCommon.getTableName(library, tableName, this);
    }

    /**
     * This method intends to retrieve a table name with schema. Meanwhile, if
     * there is any overriden, it should take place. Flow 1) To get table name
     * only and then add "TableDAM" as suffix as it is the pattern stored in
     * overrides 2) If there is overriden, then get the table and library
     * overriden 3) If libaray name is specific one, then return library.table
     * 4) Otherwise, derive schema by calling getTableName 5) return
     * schema.table
     * 
     * @param tableName
     *            - pure table name, e.g. AAAAPF
     * @return
     */
    public String getTableNameOverriden(final String tableName) {
        Object[] overrides = getOverridesWithoutSuffix(tableName);
        if (overrides == null) {
            return getTableName(tableName);
        } else {
            return getTableName(overrides[ortToFile].toString());
        }
    }

    /**
     * @return The list of temporary tables. See {@link #temporaryTables).
     */
    public StringArrayList getTemporaryTables() {
        return temporaryTables;
    }

    /**
     * Returns the Schema for Temporary Tables. If passed QTEMP, returns
     * AppVars.TEMP_SCHEMA, otherwise return the passed schema.
     * 
     * @param schema
     *            - Schema to be changed if required
     * @return String - the schema.
     */
    public String getTempSchema(final String schema) {
        if ("QTEMP".equalsIgnoreCase(schema)) {
            return TEMP_SCHEMA;
        } else {
            return schema;
        }
    }

    /**
     * @return the terminalId
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Get txConnection.
     * 
     * @return return null if the connection is hold but closed.
     */
    public Connection getTxConnection() {
        try {
            if (txConnection != null && txConnection.isClosed()) {
                txConnection = null;
            }
            return txConnection;
        } catch (SQLException e) {
            LOGGER.error("SQLException:\n", e);
            return null;
        }
    }

    /**
     * Obtain access to the user library list. You can replace the contents, but
     * you cannot replace the object.
     * 
     * @return The user library list.
     */
    public StringArrayList getUserLibraryList() {
        StringArrayList tempList = new StringArrayList(libraryList.toArray());

        // remove current library
        tempList.remove(0);

        return tempList;
    }

    public boolean isClosed(final ResultSet rs) {
        try {
            return rs == null || rs.isClosed();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * check if commitment control is started.
     * 
     * @return
     */
    @Override
    public boolean isCommitControlStarted() {
        return txConnection != null;
    }

    /**
     * Returns the end of file indicator for the last file operation. See
     * {@link #eof} for more details.
     * 
     * @return End of file reached, or not.
     */
    public boolean isEOF() {
        return eof;
    }

    /**
     * Returns the end of file indicator for nominated file. See {@link #eof}
     * for more details.
     * 
     * @return End of file reached, or not.
     */
    public boolean isEOF(final FileCode fc) {
        return fc.isEof();
    }

    /**
     * Returns the exact match flag for the last setll or lookup operation.
     * 
     * @return Exact match, or not.
     */
    public boolean isExactMatch() {
        return exactMatch;
    }

    /**
     * Returns the record found flag for the last file operation. See
     * {@link #recordFound} for more details.
     * 
     * @return Record found, or not.
     */
    public boolean isFound() {
        return recordFound;
    }

    /**
     * Returns the record found flag for the last file operation. See
     * {@link #recordFound} for more details.
     * 
     * @param fc
     *            - File referenced
     * @return Record found, or not.
     */
    public boolean isFound(final FileCode fc) {
        return fc.recordFound;
    }

    // Dummy
    public boolean isImmediateCommit() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Returns the RPG File Error indicator. This is set on if an error occurs
     * on a file operation with an extender.
     * <p>
     * Eg open(e)
     * 
     * @return true=File error occurred, false=all OK
     */
    public boolean isInError() {
        return COBOLFileError;
    }

    /**
     * Returns the end of file indicator for nominated file. See {@link #eof}
     * for more details.
     * 
     * @return End of file reached, or not.
     */
    public boolean isOpen(final FileCode fc) {
        return fc.isOpen();
    }

    /**
     * Check is page control keys (pagedown/rollup, pageup/rolldown) are enabled
     * For conditional indicators, only AND is handled right now. If needs OR
     * relation, this method needs to be amended.
     * 
     * @param pageControlName
     *            ROLLUP/ROLLDOWN
     * @return boolean
     */
    private boolean isPageControlEnabled(final String pageControlName) {
        /* Key is not defined */
        if (!fieldChangeIndicators.containsKey(pageControlName)) {
            return false;
        }
        /*
         * Value of inds could be:<br> 1) Indicator<br> 2) Indicator[]<br> 3)
         * Object[] (0=String/String[], 1=Indicator/Indicator[]
         */
        Object inds = fieldChangeIndicators.get(pageControlName);
        /* No condition, then return true */
        if (inds == null) {
            return true;
        } else if (inds instanceof Indicator) {
            return ((Indicator) inds).isOn();
        } else if (inds instanceof Indicator[]) {
            return isAllIndicatorsOn((Indicator[]) inds);
        } else if (inds instanceof Object[]) {
            // 0=String/String[], 1=Indicator/Indicator[]
            for (Object obj : (Object[]) inds) {
                if (obj == null) {
                    continue;
                } else if (obj instanceof Indicator) {
                    return ((Indicator) obj).isOn();
                } else if (obj instanceof Indicator[]) {
                    return isAllIndicatorsOn((Indicator[]) obj);
                }
            }
            // No indicator defined at all, then return false;
            return true;
        } else {
            // Unknown cases, then assume no indicator are defined, return true;
            return true;
        }
    }

    /**
     * Check if pagedown/rollup is enabled
     * 
     * @return
     */
    public boolean isPagedownEnabled() {
        return isPageControlEnabled(ROLLUP);
    }

    /**
     * Check if pageup/rolldown is enabled
     * 
     * @return
     */
    public boolean isPageupEnabled() {
        return isPageControlEnabled(ROLLDOWN);
    }

    /**
     * Check if specified schema is QTEMP, regardless letter case
     * 
     * @param schema
     * @return
     */
    public boolean isTempSchema(final String schema) {
        if (schema == null) {
            return false;
        } else {
            String upperCaseSchema = schema.trim().toUpperCase();
            return upperCaseSchema.startsWith("QTEMP") || upperCaseSchema.startsWith(AppVars.TEMP_SCHEMA);
        }
    }

    /**
     * @param e
     */
    public void logException(final Exception e) {
        progds.prevException = progds.lastException;
        progds.lastException = e;
        progds.exceptionData.set(e.toString());
    }

    /**
     * implements CL command OVRDSPF FILE(NAME) SHARE(*YES).
     * 
     * @see com.quipoz.framework.util.ISeriesScreenVarsSharedStore for detailed
     *      doco.
     * @param name
     *            - name of a resource, being shared by this instance.
     * @param requestor
     *            - id of caller program (this/ROUTINE), same as for
     *            deleteOverride() method above
     */
    public void overrideDisplayScreen(final String name, final Object screenModel, final Object requestor) {
        ISeriesScreenVarsSharedStore svss = getSharedScreens();
        svss.share(requestor, name, screenModel);
    }

    /**
     * Equivalent to {@link #overrideTable(Object, Object, String)} with the
     * last parameter null.
     */
    public void overrideTable(final Object fileName, final Object override) {
        overrideTable(fileName, override, null);
    }

    /**
     * Overrides a DB2 table that is used in a nominated DAM to use a different
     * table that has the same columns. An example might be to use a policy
     * history table instead of a policy table.
     * 
     * @param fileName
     *            - the name of the DAM to be overridden or the DAM Class. That
     *            is, supply "ACCT" or ACCTDam.class.
     * @param override
     *            - the name of the table to be used instead or the DAM Class.
     *            If supplied as a String, it can be a qualified or unqualifeid
     *            table name.
     *            <p>
     *            Ie the following are valid <br>
     *            ACCTTableDam.class <br>
     *            "ACCT" <br>
     *            "BACKUP.ACCT"
     */
    public void overrideTable(Object fileName, Object override, Object member) {

        final String HERE = ROUTINE + " overrideTable";

        String originalFile = fileName.toString();

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        if (override == null) {
        } else if (override instanceof String) {
            if (override.toString().indexOf('/') > 0) {
                override = override.toString().replace('/', '.');
            }
            if (override.toString().equalsIgnoreCase(FILE)) {
                override = QPUtilities.removeTrailing((String) fileName, "TableDAM");
            }
            if (override.toString().startsWith(LIBL)) {
                override = QPUtilities.removeLeading(override.toString(), LIBL + '.');
            }
            /* Added by wayne 20090927.
             * When QTEMP lib is used, replace it with the actual user in Oracle.
             */
			if (override.toString().startsWith("QTEMP")){
				COBOLAppVars appVars = (COBOLAppVars) AppVars.getInstance();
				override = override.toString().replaceFirst("QTEMP", getTempSchema("QTEMP"));
				if (QPBaseDataSource.DATABASE_SQLSERVER.equals(appVars.getAppConfig().getDatabaseType())) {					
					override = override.toString().replaceFirst("\\.", ".#");
				} 
			} else if (override.toString().startsWith(getTempSchema("QTEMP"))) {
				COBOLAppVars appVars = (COBOLAppVars) AppVars.getInstance();
				if (QPBaseDataSource.DATABASE_SQLSERVER.equals(appVars.getAppConfig().getDatabaseType())) {
					override = override.toString().replaceFirst("\\.", ".#");
				}
			}
        } else {
            throw new RuntimeException("Parameter override must be a String or null, but not a "
                    + fileName.getClass().getSimpleName());
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /*
         * If this is a "delete" request, and there's nothing there, nothing to
         * do.
         */
        if (stuff == null && override == null) {
            return;
        }

        /* OK, this is a new or updated override. */
        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        /*
         * Now check out the Member column. This might beFIRST orLAST (orALL,
         * but that isn't catered for yet).
         */
        if ("*ALL".equals(member)) {
            throw new RuntimeException("MEMBER(*ALL) not catered for yet.");
        }

        if ("*FIRST".equals(member) || "*LAST".equals(member)) {
            String tableName = originalFile.toString();
            if (override != null) {
                tableName = override.toString();
            }
            String[] tn = FileCode.deriveSchema(tableName);
            tableName = getTableName(tn[0], tn[1]);
            tn = FileCode.deriveSchema(tableName);
            String fwschema = tn[0];
            if (!fwschema.equals("SESSION")) {
                fwschema = getAppConfig().getFwSchema();
            }

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String required = "ASC";
            if ("*LAST".equals(member)) {
                required = "DESC";
            }
            // TODO When working, change so this SQL does not get counted in FW
            // trace
            String sql1 = "SELECT MAX_MEMBERS FROM " + fwschema + ".MEMBER_TABLE WHERE CREATOR=? AND TABLE_NAME=?";
            String sql2 = "SELECT MEMBER_NAME FROM " + fwschema
                    + ".MEMBER_CONTROL WHERE CREATOR=? AND TABLE_NAME=? ORDER BY CREATION_DATE " + required
                    + ", MEMBER_NAME " + required;
            try {
                conn = getTempDBConnection(HERE);
                ps = prepareStatement(conn, sql1);
                setDBString(ps, 1, tn[0]);
                setDBString(ps, 2, tn[1]);
                rs = executeQuery(ps);
                if (fetchNext(rs)) {
                    freeDBConnectionIgnoreErr(null, ps, rs);
                    ps = prepareStatement(conn, sql2);
                    setDBString(ps, 1, tn[0]);
                    setDBString(ps, 2, tn[1]);
                    rs = executeQuery(ps);
                    if (fetchNext(rs)) {
                        member = rs.getString(1);
                    }
                    /* Not found, so member == filename. */
                    else {
                        member = tn[1];
                    }
                }
                /*
                 * Table does not have members, so ignore the member. It really
                 * means member == filename, but we don't implement it like
                 * that.
                 */
                else {
                    member = null;
                }
            } catch (SQLException se) {
                throw new RuntimeException(se);
            } finally {
                freeDBConnectionIgnoreErr(conn, ps, rs);
            }
        }

        stuff[ortToFile] = override;
        stuff[ortMember] = member;

        /* If that means there is nothing left, delete the override. */
        boolean allNull = true;
        for (int i = 0; i < stuff.length; i++) {
            allNull = allNull && (stuff[i] == null);
        }
        if (allNull) {
            overriddenTables.remove(fileName);
            return;
        }

        overriddenTables.put(fileName, stuff);

        if (isTableSchemaEnabled())
        {
        	addTableSchema(override.toString());
        }
    }

    /**
     * Boolean version as per Emma Tonkin. See below.
     * 
     * @param fileName
     * @param inhibit
     *            true=='*YES'; false=='*NO'
     */
    public void overrideTableInhibitWrite(final Object fileName, final boolean inhibit) {
        overrideTableInhibitWrite(fileName, inhibit ? STAR_YES : STAR_NO);
    }

    /**
     * Overrides the file and stops data updates from occurring. If "yes",
     * inserts, updates and deletes will have no effect.
     * 
     * @param fileName
     * @param override
     */
    public void overrideTableInhibitWrite(Object fileName, String inhibit) {

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        inhibit = QPUtilities.defaultUCTrim(inhibit, "YES");
        inhibit = QPUtilities.strip(inhibit, '*');

        if (!inhibit.equals("YES") && !inhibit.equals("NO")) {
            throw new RuntimeException("Share mode must be YES or NO, not '" + inhibit + "'");
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /*
         * If this is a "delete" request, and there's nothing there, nothing to
         * do.
         */
        if (stuff == null && inhibit.equals("NO")) {
            return;
        }

        /* OK, this is a new or updated inhibit write. */
        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        /*
         * If this is a "delete" request delete the table shareMode. If that
         * means there is nothing left, delete the shareMode.
         */
        if (inhibit.equals("NO")) {
            stuff[ortInhibitWrite] = null;

            boolean allNull = true;
            for (int i = 0; i < stuff.length; i++) {
                allNull = allNull && (stuff[i] == null);
            }

            if (allNull) {
                overriddenTables.remove(fileName);
                return;
            }
        }

        stuff[ortInhibitWrite] = inhibit;
        overriddenTables.put(fileName, stuff);
    }

    /**
     * Overrides the LVLCHK for a table NOTE does nothing not implemented.
     * Levels do not apply for relational tables.
     * 
     * @param fileName
     * @param check
     */
    public void overrideTableLevelCheck(final Object fileName, final String check) {
        /* Avoid IDE "field not used" warning */
        if (1 == 0) {
            fileName.getClass();
            check.getClass();
        }
    }

    /**
     * Overrides the file position. Usually used for restart processing.
     * 
     * @param fileName
     * @param position
     */
    public void overrideTablePosition(Object fileName, String position) {

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        if (position != null) {
            position = position.trim();
            if (position.length() == 0) {
                position = null;
            }
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /*
         * If this is a "delete" request, and there's nothing there, nothing to
         * do.
         */
        if (stuff == null && position == null) {
            return;
        }

        /* OK, this is a new or updated position. */
        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        stuff[ortPosition] = position;

        /*
         * If this is a "delete" request delete the table shareMode. If that
         * means there is nothing left, delete the shareMode.
         */
        boolean allNull = true;
        for (int i = 0; i < stuff.length; i++) {
            allNull = allNull && (stuff[i] == null);
        }

        if (allNull) {
            overriddenTables.remove(fileName);
            return;
        }

        overriddenTables.put(fileName, stuff);
    }

    /**
     * Overrides the scope of a table override. This is the OVRSCOPE part of the
     * RPG OVRDBF command. If OVRSCOPE(*JOB) is specified, whatever overrides
     * are current will apply for the whole job.
     * <p>
     * While technically scope could be downgraded, this is not currently
     * supported and anything other than JOB will throw an exception.
     * <p>
     * The scope of JOB is implemented in the following manner: when a program
     * ends and the stack of overrides is restored,
     * 
     * @param fileName
     * @param scope
     */
    public void overrideTableScope(Object fileName, String scope) {

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        scope = QPUtilities.defaultUCTrim(scope, "JOB");
        scope = QPUtilities.strip(scope, '*');

        if (!scope.equals("JOB")) {
            throw new RuntimeException("Scope must be JOB, not '" + scope + "'");
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /* OK, this is a new or updated shareMode. */
        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        stuff[ortScope] = scope;
        overriddenTables.put(fileName, stuff);

    }

    /**
     * Boolean version as per Emma Tonkin. See below.
     * 
     * @param fileName
     * @param secure
     *            true=='*YES'; false=='*NO'
     */
    public void overrideTableSecure(final Object fileName, final boolean secure) {
        overrideTableSecure(fileName, secure ? STAR_YES : STAR_NO);
    }

    /**
     * Secures the overridden file against effects of previously specified
     * overrides. Not implemented!
     * 
     * @param fileName
     * @param secure
     */
    public void overrideTableSecure(final Object fileName, final String secure) {
    }

    /**
     * Boolean version as per Emma Tonkin. See below.
     * 
     * @param fileName
     * @param shareMode
     *            true=='*YES'; false=='*NO'
     */
    public void overrideTableShare(final Object fileName, final boolean shareMode) {
        overrideTableShare(fileName, shareMode ? STAR_YES : STAR_NO);
    }

    /**
     * Overrides the SHARE path for a table. This is the SHARE part of the RPG
     * OVRDBF command. If SHARE(YES) is specified, a file is only opened once.
     * So, if ProgA opened the file and read 1000 records, then called ProgB,
     * and ProgB opened the file and read a record, it would get the 1001st
     * record.
     * 
     * @param fileName
     * @param shareMode
     */
    public void overrideTableShare(Object fileName, String shareMode) {

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        shareMode = QPUtilities.defaultUCTrim(shareMode, "YES");
        shareMode = QPUtilities.strip(shareMode, '*');

        if (!shareMode.equals("YES") && !shareMode.equals("NO")) {
            throw new RuntimeException("Share mode must be YES or NO, not '" + shareMode + "'");
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /*
         * If this is a "delete" request, and there's nothing there, nothing to
         * do.
         */
        if (stuff == null && shareMode.equals("NO")) {
            return;
        }

        /* OK, this is a new or updated shareMode. */
        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        /*
         * If this is a "delete" request delete the table shareMode. If that
         * means there is nothing left, delete the shareMode.
         */
        if (shareMode.equals("NO")) {
            stuff[ortShare] = null;
            stuff[ortShareOpen] = null;

            boolean allNull = true;
            for (int i = 0; i < stuff.length; i++) {
                allNull = allNull && (stuff[i] == null);
            }

            if (allNull) {
                overriddenTables.remove(fileName);
                return;
            }
        }

        stuff[ortShare] = shareMode;
        stuff[ortShareOpen] = new Integer(0);
        overriddenTables.put(fileName, stuff);

    }

    /**
     * Overrides a DB2 table that is used in a nominated DAM to include an
     * additional bit of SQL. Eg, add "POLICY_TYPE = 'Q'.
     * 
     * @param fileName
     *            - the name of the DAM to be overridden or the DAM Class. That
     *            is, supply "ACCT" or ACCTDam.class.
     * @param override
     *            - the overriding SQL. The full syntax is " WHERE sql AND "
     *            where 'sql' is any valid SQL that could be applied to the
     *            table, but the leading " WHERE " and trailing " AND " can be
     *            omitted.
     */
    public void overrideTableWhere(Object fileName, String override) {

        if (fileName instanceof String || fileName instanceof FixedLengthStringData) {
            String fn = fileName.toString().trim();
            if (!fn.endsWith("TableDAM")) {
                fn = fn.substring(0, 1).toUpperCase() + fn.substring(1).toLowerCase() + "TableDAM";
            }
            fileName = fn;
        } else if (fileName instanceof Class) {
            fileName = QPUtilities.getUnqualifiedClassName((Class) fileName);
        } else {
            throw new RuntimeException(
                    "Parameter filename must be a String or a FixedLengthStringData or a Class, but not a "
                            + fileName.getClass().getSimpleName());
        }

        /* Allocate, if needed */
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }

        /* Get the current override, if any */
        Object[] stuff = (Object[]) overriddenTables.get(fileName);

        /*
         * If this is a "delete" request, and there's nothing there, nothing to
         * do.
         */
        if (stuff == null && override == null) {
            return;
        }

        if (stuff == null) {
            stuff = new Object[overriddenSize];
        }

        /*
         * OK, this is a new or updated override. Sanity-clean the passed
         * String.
         */
        if (override != null) {
            override = override.trim();
            if (!override.toUpperCase().startsWith("WHERE")) {
                override = "WHERE " + override;
            }
            if (!override.toUpperCase().endsWith("AND")) {
                override = override + " AND";
            }
            stuff[ortWhere] = " " + override + " ";
        }

        /* If that means there is nothing left, delete the override. */
        boolean allNull = true;
        for (int i = 0; i < stuff.length; i++) {
            allNull = allNull && (stuff[i] == null);
        }
        if (allNull) {
            overriddenTables.remove(fileName);
            return;
        }

        overriddenTables.put(fileName, stuff);
    }

    /**
     * Currently, it acts as same as prepareStatement. However, this method is
     * supposed to handle the case where SQL statement is generated manually.
     * Therefore, the table name hasn't been applying the table which could be
     * overridden. This methos is only supposed to be called in a SQL-embeded
     * program.
     * 
     * @param conn
     * @param aSql
     * @return
     * @throws SQLException
     */
    public PreparedStatement prepareStatementEmbeded(final Connection conn, final String aSql) throws SQLException {
        return prepareStatement(conn, aSql);
    }

    /**
     * Prepare statement with possibly member name is set.
     * 
     * @param conn
     * @param aSql
     *            - SQL statement with table name overriden.
     * @param tableName
     *            - original Table name which is not replaced by the table name
     *            overridden. It is used to detect if there is overridden on
     *            member. If yes, then the SQL passed in will be revised by
     *            calling reviseSqlWithMember(..)
     * @return
     * @throws SQLException
     */
    public PreparedStatement prepareStatementEmbeded(final Connection conn, final String aSql, final String tableName)
            throws SQLException {
        // To revise SQL if Member name is overriden
        return prepareStatement(conn, reviseSqlWithMember(aSql, tableName));
    }

    /**
     * Convenient method to avoid compilation error caused by transformation of
     * PUTOVR keyword in Display Files. As described in IBM technical document,
     * This record-level keyword to permit the override of either display
     * attributes or data contents (or both) of specific fields within a record
     * displayed on a workstation device. By using PUTOVR, you can reduce the
     * amount of data sent to the display device. It's not necessary to
     * implement it at the moment as it won't affect the result of display.
     */
    public void putOvr() {
        // Do nothing unless there is any further requirements found
    }

    public Object putRoutine(Object k, Object v) {
        return routines.put(k, v);
    }

    /**
     * Emulates AS400 QMHRTVM. Delegates processing to
     * <code>retrieveMessage(...)</code> above.
     */
    public void qRetrieveMessage(final BaseData msg, final BaseData msglen, final Object dummy1_formatName,
            final Object msgid, final Object msgf, final Object msgda, final Object dummy2_daLength,
            final Object dummy3_subVals, final Object dummy4_controlChars, final Object dummy5_errorCode) {
        retrieveMessage(msgid, msgf, msgda, msg, msglen);
    }

    /**
     * Really free a DB2 connection. The method
     * {@link #freeDBConnection(Connection conn)} can sometimes be overridden.
     */
    @Override
    public void reallyFreeDBConnection(final Connection conn) throws Exception {

        if (conn == qtempConnection) {
            return;
            // System.err.println("Attempt to free Temp connection ignored.");
        }

        if (getAppConfig().getServer().equals("localswing")) {
            QPBaseDataSource.unsetDBConnection(conn);
        } else {
            QPPooledDataSource.unsetDBConnection(conn);
        }
    }

    // Rightshore - sbhaumik - issue 1915 - Start
    public void receiveDiagnosticMessage(final BaseData msgdata, final BaseData msgid) {
        receiveDiagnosticMessage(null, msgid, msgdata);
    }

    // TODO requires coding
    /**
     * Implements a variant of RCVMSG but does NOTHING. In the code so far
     * encountered, this variant is only used to copy messages from the current
     * program to the program that called it. This message stacking has NOT been
     * implemented and this method just clears the varaibles that are passed to
     * it. Future work may require a more comprehensive implementation.
     * 
     * @param msgdta
     * @param msgid
     * @param msgf
     * @param msgflib
     */
    public void receiveDiagnosticMessage(final BaseData msg, final BaseData msgid, final BaseData msgdata) {
        if (msg != null) {
            msg.clear();
        }
        if (msgid != null) {
            msgid.clear();
        }
        if (msgdata != null) {
            msgdata.clear();
        }
    }

    // TODO requires coding
    /**
     * Implements a variant of RCVMSG but does NOTHING.
     */
    public void receiveExceptionMessage() {
    }

    // Rightshore - sbhaumik - issue 1906 - Start
    public void receiveExceptionMessage(final BaseData msgId) {
    }

    /**
     * This method is created as an emulation of RCVMSG command. RCVMSG
     * MSGQ(QTEMP/SBMTHREAD) MSGTYPE(*LAST) + MSGDTA(&MSGDTA) MSGID(&MSGID)
     * MSGF(&MSGF) + MSGFLIB(&MSGLIB)
     * 
     * @param msgQ
     * @param msgType
     * @param msgData
     * @param msgId
     * @param msgF
     * @param msgLib
     * @param messageId
     * @param messageData
     *            Need to add the functionality of this method.
     */
    public void receiveExceptionMessage(String msgQ, String msgType, FixedLengthStringData msgData,
            FixedLengthStringData msgId, FixedLengthStringData msgF, FixedLengthStringData msgLib,
            FixedLengthStringData messageId, FixedLengthStringData messageData) {
    }

    // TODO requires coding
    /**
     * Implements a variant of RCVMSG but does NOTHING. In the code so far
     * encountered, this variant is only used to copy messages from the current
     * program to the program that called it. This message stacking has NOT been
     * implemented and this method just clears the varaibles that are passed to
     * it. Future work may require a more comprehensive implementation.
     * 
     * @param msgdta
     * @param msgid
     * @param msgf
     * @param msgflib
     */
    public void receiveMessage(final BaseData msgdta, final BaseData msgid, final BaseData msgf, final BaseData msgflib) {
        msgdta.clear();
        msgid.clear();
        msgf.clear();
        msgflib.clear();
    }

    @Override
    public void recreateTransientFields() {
        super.recreateTransientFields();

        if (arrays == null) {
            arrays = new Hashtable();
        }
        if (tableMetaData == null) {
            tableMetaData = new Hashtable(500);
        }
        if (overriddenTables == null) {
            overriddenTables = new Hashtable();
        }
        if (dataarea == null) {
            dataarea = new DataArea();
        }
        if (sharedScreenVars == null) {
            sharedScreenVars = new ISeriesScreenVarsSharedStore();
        }
        if (temporaryTables == null) {
            temporaryTables = new StringArrayList();
        }
        if (smartFileCodeSessionVariablesMap == null) {
            smartFileCodeSessionVariablesMap = new HashMap<Object, Object>();
        }
        
       String jsonStr = getAppConfig().getAdditionalConfigStr();
		if(!jsonStr.isEmpty()){
		 getAppConfig().setConfiguredItem(new JSONObject(jsonStr));
		}
    }

    /**
     * Update any variables as needed just before going to a new screen. This
     * includes, among other things, resetting the indicators.
     */
    @Override
    public void reinitBeforeNewScreen() {
        for (int i = 0; i < 100; i++) {
            IndicArea[i].set(false);
        }
        indlr.set(false);
    }

    /**
     * @param pLibrary
     *            The name of the library being removed.
     */
    public void removeLibraryListEntry(final BaseData pLibrary) {
        removeLibraryListEntry(pLibrary.toString());
    }

    /**
     * <p>
     * The purpose of this function is to implement RPG RMVLIBLE command. <br>
     * It removes a library name from the library list.<br>
     * <br>
     * The following escape messages are implemented:
     * <ol>
     * <li>CPF2104: Library &1 not removed from the library list.
     * </ol>
     * 
     * @param pLibrary
     *            The name of the library being removed.
     */
    public void removeLibraryListEntry(final String pLibrary) {
        int index;
        if (pLibrary.equalsIgnoreCase(QTEMP)) {
            index = libraryList.indexof(getTempSchema(QTEMP));
        } else {
            index = libraryList.indexof(getTempSchema(pLibrary));
        }
        if (index < 0) {
            addExtMessage("CPF2104", "Library " + pLibrary + " not removed from the library list.");
            return;
        }

        libraryList.remove(index);
    }

    /**
     * Overload method for removeMessages(..) to accept a FixedLengthStringData
     * object.
     * 
     * @param scope
     */
    public void removeMessages(final FixedLengthStringData scope) {
        removeMessages(scope.toString());
    }

    // TODO may need more specific coding.
    /**
     * Implements RPG RMVMSG.
     * 
     * @param pgmqueue
     *            - typically *PRV or *SAME, but currently ignored.
     * @param scope
     *            is supposed to be supplied, but is ignored; all messages will
     *            be cleared. "Cleared" in this context does NOT mean the
     *            messages will be deleted, but will be flagged as
     *            "All messages have been seen".
     */
    public void removeMessages(final Object pgmqueue, final String scope) {
        getNewMessages();
    }

    /**
     * Implements RPG RMVMSG. Scope is supposed to be supplied, but is ignored;
     * all messages will be cleared. "Cleared" in this context does NOT mean the
     * messages will be deleted, but will be flagged as
     * "All messages have been seen".
     * 
     * @param scope
     */
    public void removeMessages(final String scope) {
        getNewMessages();
    }

    // TODO may need more specific coding.
    /**
     * Implements RPG RMVMSG.
     * 
     * @param pgmqueue
     *            - typically *PRV or *SAME, but currently ignored.
     * @param messageQueue
     *            - typically *PGMQ, but currently ignored.
     * @param scope
     *            is supposed to be supplied, but is ignored; all messages will
     *            be cleared. "Cleared" in this context does NOT mean the
     *            messages will be deleted, but will be flagged as
     *            "All messages have been seen".
     */
    public void removeMessages(final String pgmqueue, final String messageQueue, final String scope) {
        getNewMessages();
    }

    public Object removeRoutine(Object k) {
        return routines.remove(k);
    }

    /**
     * @param savedOtherInds
     */
    public void restoreAllInds(final boolean[] savedInds) {
        if (savedInds != null) {
            int i = 0;
            for (i = 0; i < IndicArea.length; i++) {
                IndicArea[i].set(savedInds[i]);
            }
            for (int j = 0; j < RPGOA.length; j++) {
                RPGOA[j].set(savedInds[i++]);
            }
        }
        return;
    }

    /**
     * Restores all IndicArea indicators to the values of the savedInds
     * parameter.
     * 
     * @param start
     * @param savedInds
     */
    public void restoreInds(final BaseData savedInds) {
        // if arr is not specified, do nothing.
        if (savedInds == null) {
            return;
        }
        char[] inds = savedInds.toString().toCharArray();
        for (int i = 0; i < inds.length && i < 100; i++) {
            IndicArea[i].set(inds[i]);
        }
    }

    /**
     * Restores all IndicArea indicators to the values of the savedInds
     * parameter from a given starting index.
     * 
     * @param start
     * @param savedInds
     */
    public void restoreInds(final int start, final BaseData savedInds) {
        if (savedInds == null) {
            return;
        }
        char[] inds = savedInds.toString().toCharArray();
        for (int i = start; i < inds.length && i < 100 - start; i++) {
            IndicArea[i].set(inds[i]);
        }
    }

    /**
     * Restores all IndicArea indicators to the values of the savedInds
     * parameter. In this case, the length of saveInds is 99,while IndicArea is
     * 100 long. Hence there is one bit mismatching here, which needs to be
     * handled.
     * 
     * @param start
     * @param savedInds
     */
    public void restoreInds99(final BaseData savedInds) {
        // if arr is not specified, do nothing.
        if (savedInds == null) {
            return;
        }
        char[] inds = savedInds.toString().toCharArray();
        for (int i = 1; i <= inds.length && i < 100; i++) {
            IndicArea[i].set(inds[i - 1]);
        }
    }

    /**
     * @param savedOtherInds
     */
    public void restoreOtherInds(final BaseData savedOtherInds) {
        char[] inds = savedOtherInds.toString().toCharArray();
        for (int i = 0; i < inds.length && i < RPGOA.length; i++) {
            RPGOA[i].set(inds[i]);
        }
    }

    /**
     * simply invoke COBOLAppVars.retrieveMessage(), currently ignores the rest
     * parameters. and then return an RPGStructure; <br>
     * implementation of QMHRTVM
     * 
     * @param oMsgInfo
     *            - output, message information
     * @param iMsgLength
     *            - input, length of message returned
     * @param iFormat
     *            - input, information format name
     * @param iMsgId
     *            - input, message id
     * @param iMsgFile
     *            - input, message file name
     * @param iReplacement
     *            - input, replacement data
     * @param iLenghtReplacement
     *            -input, length of replacement
     * @param iSubstitution
     *            - input, if replace by replacement data *YES/*NO
     * @param iFmtCtrl
     *            - input, if returen control chractre. *YES/*NO
     */
    public void retrieveMessage(final BaseData oMsgInfo, int iMsgLength, final String iFormat, final String iMsgId,
            final String iMsgFile, final Object iReplacement, final int iLenghtReplacement, final String iReplace,
            final String iFmtCtrl) {

        oMsgInfo.clear();
        // validation

        if (iMsgLength < 20) {
            // minimum length is 8
            iMsgLength = 20;
        }
        // only RTVM0100 is supported currently
        if (iFormat == null || !iFormat.equals("RTVM0100")) {
            addExtMessage("CPF24AB");
            return;
        }

        if (iMsgId == null || iMsgId.trim().length() < 7) {
            addExtMessage("CPF24A7");
            return;
        }

        if (iReplace == null || (!iReplace.trim().equals("*YES") && !iReplace.trim().equals("*NO"))) {
            addExtMessage("CPF24AA");
            return;
        }

        if (iFmtCtrl == null || (!iFmtCtrl.trim().equals("*YES") && !iFmtCtrl.trim().equals("*NO"))) {
            addExtMessage("CPF24AB");
            return;
        }

        // retrieve static message
        StringBase msgInfo = new StringData();
        IntegerData rtvLength = new IntegerData();
        retrieveMessage(iMsgId, iMsgFile, iReplacement, msgInfo, rtvLength);
        if (rtvLength.getData().intValue() > iMsgLength) {
            oMsgInfo.set(msgInfo.getData().substring(0, iMsgLength));
        } else {
            oMsgInfo.set(msgInfo.getData());
        }

    }

    /**
     * Emulates RPG RTVMSG.
     * 
     * @param msgid
     *            - Specifies the message identifier of the predefined message
     *            being retrieved from the specified message file.
     * @param msgf
     *            - Specifies the qualified name of the message file that
     *            contains the predefined message being retrieved. As currently
     *            coded, ignored.
     * @param msgda
     *            - Specifies the substitution values that are used in the
     *            retrieved message if the predefined message contains
     *            substitution variables.
     * @param msg
     *            - Specifies the variable in the program into which the
     *            first-level message text from the retrieved message is copied.
     * @param msglen
     *            - Specifies the variable in the program into which the total
     *            length of the text available is copied.
     */
    public void retrieveMessage(final Object msgid, final Object msgf, final Object msgda, final BaseData msg,
            final BaseData msglen) {
        String s = "";
        if (msg != null && msgid != null) {
            s = getDBMessage(msgid, msgda);
            msg.set(s);
            if (msglen != null) {
                msglen.set(s.length());
            }
        }
    }

    /**
     * Retrieves the data from a pass-through
     * 
     * @param receieverValue
     *            The reciever variable
     * @param receiverLength
     *            The length of the receiver variable
     * @param actualLength
     *            The actual length of the data
     */
    public void retrievePassthroughData(final FixedLengthStringData receieverValue, final BinaryData receiverLength,
            final BinaryData actualLength) {
        actualLength.set(ptd.length());
        if (receiverLength.toInt() == 0) {
            receieverValue.set(ptd);
        } else {
            receieverValue.setsub1string(1, receiverLength, ptd);
        }
    }

    /**
     * Retrieves a value from a specific printer file
     * 
     * @param file
     *            name of the file to get the value from
     * @param key
     *            key of the value to retrieve
     * @return value from HashMap
     */
    public String retrievePrinterFileValue(final String file, final String key) {
        HashMap printerFile = (HashMap) printerFiles.get(file);
        String value = (String) printerFile.get(key);
        return value;
    }

    /**
     * Emulates RPG RTVMSG for second level messages
     * 
     * @param msgid
     *            - Specifies the message identifier of the predefined message
     *            being retrieved from the specified message file.
     * @param msgf
     *            - Specifies the qualified name of the message file that
     *            contains the predefined message being retrieved. As currently
     *            coded, ignored.
     * @param msgda
     *            - Specifies the substitution values that are used in the
     *            retrieved message if the predefined message contains
     *            substitution variables.
     * @param seclvl
     *            - - Specifies the variable in the program into which the
     *            second-level message text from the retrieved message is
     *            copied.
     * @param msglen
     *            - Specifies the variable in the program into which the total
     *            length of the text available is copied.
     */
    public void retrieveSecondMessage(final Object msgid, final Object msgf, final Object msgda, final BaseData seclvl,
            final BaseData msglen) {
        String s = "";
        if (seclvl != null && msgid != null) {
            s = getDBMessage(msgid, msgda);
            seclvl.set(s);
            if (msglen != null) {
                msglen.set(s.length());
            }
        }
    }

    /**
     * Get the value of the user profile, see userProfile definition.
     * 
     * @param profile
     *            Specifies the profile of a user
     * @param value
     *            Specifies the returned value related to the profile
     */
    public void retrieveUserProfile(final String profile, final BaseData value) {

    }

    public void retrieveUserProfile(final String profile, final BaseData value, final AppVars av) {

    }

    /**
     * Revise SQL statement with a condition of member_name.
     * "WHERE MEMBER_NAME = ? " OR " WHERE MEMBER_NAME = ? AND (" + original
     * conditions + ')'
     * 
     * @param aSql
     * @param tableName
     * @return
     */
    private String reviseSqlWithMember(final String aSql, final String tableName) {
        Object[] overrides = getOverridesWithoutSuffix(tableName);
        // No overidden or no member overriden, then do nothing
        if (overrides == null || overrides[ortMember] == null) {
            return aSql;
        }
        // Otherwise, to find Where clause and revise it

        String realTableName;
        if (overrides == null) {
            realTableName = getTableName(tableName);
        } else {
            realTableName = getTableName(overrides[ortToFile].toString());
        }
        int sfIndex = aSql.indexOf(realTableName);
        if (sfIndex < 0) {
            throw new IllegalArgumentException("Not able to find table [" + realTableName + "] in the SQL specified ["
                    + aSql + ']');
        }
        int endIndex1 = sfIndex + realTableName.length() + 1;
        // Select ... from xxx.table
        String sfStr = aSql.substring(0, endIndex1);
        // The rest of statement
        String theRest = aSql.substring(endIndex1).trim();
        // If there is already Where clause, and insert one
        if (theRest.startsWith("WHERE")) {
            int startOrderByIndex = theRest.indexOf("ORDER BY");
            if (startOrderByIndex != -1) {
                // Only parse the where condition. Get it after "WHERE" keyword to before "ORDER BY"
                // keyword.
                return sfStr + " WHERE MEMBER_NAME = '" + overrides[ortMember] + "' AND ("
                        + theRest.substring(5, startOrderByIndex) + ')'
                        + theRest.substring(startOrderByIndex);
            } else {
                return sfStr + " WHERE MEMBER_NAME = '" + overrides[ortMember] + "' AND (" + theRest.substring(5) + ')';
            }
        } else {
            return sfStr + " WHERE MEMBER_NAME = '" + overrides[ortMember] + "' " + theRest;
        }
    }

    /**
     * Logs a Control button as one that will set or clear an indicator
     * depending on whether or not the user changes it. See
     * {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void roll(final String name, final Indicator ind) {
        fieldChangeIndicators.put(name, new Object[] { "", ind });
    }

    /**
     * Logs a Control button as one that will set or clear an array of
     * indicators depending on whether or not the user changes it. See
     * {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void roll(final String name, final int[] inds) {
        Object[] objArray = new Object[inds.length + 1];
        objArray[0] = "";
        for (int i = 0; i < inds.length; i++) {
            objArray[i + 1] = IndicArea[inds[i]];
        }
        fieldChangeIndicators.put(name, objArray);
    }

    /**
     * implementation of ROLLBACK. The method rollbacks updates from the last
     * execution of CMMIT or ROLLBACK; simply, call connection.rollback(); Max W
     * 
     * @throws ExtMsgException
     */
    @Override
    public void rollback() throws ExtMsgException {
        // txConnection is null, means the transaction contorl has not been
        // started yet
        if (txConnection == null) {
            addExtMessage("CPF8350", "Commitment definition not found.");
        }
        // commit changes
        try {
            // txConnection.rollback();
            if (this.getHibernateSession() != null) {
                this.getHibernateSession().getTransaction().rollback();
                this.getHibernateSession().clear();
                this.getHibernateSession().getTransaction().begin();
            }
        } catch (Exception e) {
            addDiagnostic(e.getLocalizedMessage());
            addExtMessage("CPF8359", "Rollback operation failed.");
        }
    }

    /*
     * This method is <b>NOT</b> used for <b>transaction control</b> at all.
     * It's a convenient method to commit a connection. all application code
     * should not rollback connection straightly. this method check if specified
     * connection is used for transaction control; if true, then do nothing;
     * otherwise rollback the connection.
     */
    @Override
    public void rollback(Connection conn) throws ExtMsgException {
        if (conn == null || conn == txConnection) {
            return;
        } else {
            try {
                conn.rollback();
            } catch (SQLException e) {
                addDiagnostic(e.getLocalizedMessage());
                // propagate an runtime exception at the moment, as there is no
                // way to recovery from it
                throw new SQLRuntimeException(e);
            }
        }
    }

    /**
     * You can specify a response indicator with these keywords. If you do, and
     * the appropriate Page key is pressed, the OS/400 program sets on the
     * specified response indicator within the input record and returns control
     * to your program after it processes the input data. If you do not specify
     * a response indicator and the specified Page key is pressed, the OS/400
     * program performs normal input record processing. Simply use default page
     * up indicator
     */
    public void rolldown() {
        /* Indicates that there is no condition indicator */
        rolldown((Indicator) null);
    }

    /**
     * Logs a Page up as one that will set or clear an indicator depending on
     * whether or not the user changes it. See {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rolldown(final Indicator ind) {
        roll(ROLLDOWN, ind);
    }

    /**
     * Logs a Page up as one that will set or clear an indicator depending on
     * whether or not the user changes it. See {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rolldown(final int ind) {
        rolldown(IndicArea[ind]);
    }

    /**
     * Logs a Page up as one that will set or clear an array of indicators
     * depending on whether or not the user changes it. See
     * {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rolldown(final int[] inds) {
        roll(ROLLDOWN, inds);
    }

    /**
     * You can specify a response indicator with these keywords. If you do, and
     * the appropriate Page key is pressed, the OS/400 program sets on the
     * specified response indicator within the input record and returns control
     * to your program after it processes the input data. If you do not specify
     * a response indicator and the specified Page key is pressed, the OS/400
     * program performs normal input record processing. Simply, default pagedown
     * indicator is used.
     */
    public void rollup() {
        /* Indicates that there is no condition indicator */
        rollup((Indicator) null);
    }

    /**
     * Logs a Page down as one that will set or clear an indicator depending on
     * whether or not the user changes it. See {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rollup(final Indicator ind) {
        roll(ROLLUP, ind);
    }

    /**
     * Logs a Page down as one that will set or clear an indicator depending on
     * whether or not the user changes it. See {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rollup(final int ind) {
        rollup(IndicArea[ind]);
    }

    /**
     * Logs a Page down as one that will set or clear an array of indicators
     * depending on whether or not the user changes it. See
     * {@link #fieldChangeIndicators}.
     * 
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void rollup(final int[] inds) {
        roll(ROLLUP, inds);
    }

    /**
     * Saves all indicators (main and other) to a storage area which will be
     * created.
     * 
     * @return - char[].
     */
    public boolean[] saveAllInds() {
        boolean[] inds = new boolean[IndicArea.length + RPGOA.length];
        int i = 0;
        for (i = 0; i < IndicArea.length; i++) {
            inds[i] = IndicArea[i].isOn();
        }
        for (int j = 0; j < RPGOA.length; j++) {
            inds[i++] = RPGOA[j].isOn();
        }
        return inds;
    }

    /**
     * Saves all 100 of the main indicators to a storage area which will be
     * created.
     * 
     * @param start
     *            - starting indicator
     * @return - a FixedLengthStringData(100) containing the indicators.
     */
    public BaseData saveInds() {
        return saveInds(new FixedLengthStringData(100));
    }

    /**
     * Saves all 100 of the main indicators to a storage area.
     * 
     * @param start
     *            - starting indicator
     * @param arr
     *            - where to stick them
     * @return - arr, modified.
     */
    public BaseData saveInds(final BaseData arr) {
        return saveInds(0, arr);
    }

    /**
     * Saves all 100 of the main indicators to a storage area.
     * 
     * @param start
     *            - starting indicator
     * @param arr
     *            - where to stick them
     * @return - arr, modified.
     */
    public BaseData[] saveInds(final BaseData[] arr) {
        return saveInds(0, arr);
    }

    /**
     * Saves the main indicators to a storage area, starting at a given
     * indicator number.
     * 
     * @param start
     *            - starting indicator
     * @param arr
     *            - where to stick them
     * @return - arr, modified.
     */
    public BaseData saveInds(final int start, final BaseData arr) {
        // if arr is not specified, do nothing.
        if (arr == null) {
            return null;
        }
        char[] inds = new char[100];
        for (int i = start; i < 100; i++) {
            inds[i - start] = IndicArea[i].isOn() ? '1' : '0';
        }
        arr.set(new String(inds));
        return arr;
    }

    /**
     * Saves the main indicators to a storage area, starting at a given
     * indicator number.
     * 
     * @param start
     *            - starting indicator
     * @param arr
     *            - where to stick them
     * @return - arr, modified.
     */
    public BaseData[] saveInds(final int start, final BaseData[] arr) {
        int lim = Math.min(100, arr.length);
        for (int i = start; i < lim; i++) {
            if (arr[i] != null) {
                arr[i - start].set(IndicArea[i].isOn() ? "1" : "0");
            }
        }
        return arr;
    }

    /**
     * Saves the other indicators eg indlr to a storage area which will be
     * created.
     */
    public BaseData saveOtherInds() {
        return saveOtherInds(new FixedLengthStringData(RPGOA.length));
    }

    /**
     * Saves the other indicators eg indlr to a storage area.
     * 
     * @param arr
     *            - where to stick them
     */
    public BaseData saveOtherInds(final BaseData arr) {
        char[] inds = new char[RPGOA.length];
        for (int i = 0; i < RPGOA.length; i++) {
            inds[i] = RPGOA[i].isOn() ? '1' : '0';
        }
        arr.set(new String(inds));
        return arr;
    }

    // TODO implement properly
    /**
     * Sends a message to a queue as per the parms supplied. At the time of
     * writing, the only use is to send a message to the SYSOPR's queue. As the
     * role of the SYSOPR is currently undefined, I'm just going to send it to a
     * user called SYSOPR.
     */
    public void sendMessageToQueue(final Object message, final Object queue) {
        sendMessageToUser(message, queue);
    }

    /**
     * Sends a message to a user as per the parms supplied. Implements
     * SNDMSGUSR.
     * 
     * @param message
     *            - Something containing the message, typically a String or a
     *            BaseData.
     * @param user
     *            - Specifies that the message is sent to the message queue
     *            specified in the user profile for the user named on this
     *            parameter. Specific values are:
     *            <table>
     *            <tr>
     *            <td>*SYSOPR</td>
     *            <td>The message is sent to the system operator's message queue
     *            (QSYS/QSYSOPR). Any message sent to QSYSOPR automatically has
     *            a copy of the message sent to QHST.</td>
     *            </tr>
     *            <tr>
     *            <td>*REQUESTER</td>
     *            <td>The message is sent to the user profile's message queue
     *            for interactive jobs or to the system operator's message queue
     *            (QSYS/QSYSOPR) for batch jobs.</td>
     *            </tr>
     *            <tr>
     *            <td>*ALLACT</td>
     *            <td>A copy of the message is sent to the user profile message
     *            queue of each user profile with an interactive job currently
     *            running.</td>
     *            <tr>
     *            <td>user-profile-name</td>
     *            <td>Specify the user profile name of the user to whom the
     *            message is sent.</td>
     *            </table>
     */
    public void sendMessageToUser(final Object message, final Object user) {
        if (user.equals("*")) {
            addMessage(message + "");
            return;
        }
        // Updated by Max Wang.
        MessageAgent.sendMessageToUser("COBOLAppVars", null, null, message, MessageType.DIAGNOSTIC,
                new Object[] { user }, null, null);
        // String sql = "INSERT INTO "
        // + getAppConfig().getFwSchema()
        // +
        // ".USER_MESSAGES (USERID, MESSAGE, SENT, SEEN) VALUES(?, ?, CURRENT TIMESTAMP, 'N')";
        // try {
        // executeAndCommit(sql, new Object[] { user.toString(),
        // message.toString() });
        // } catch (SQLException se) {
        // throw new ExtMsgException(
        // "CPF2469 Error occurred when sending message. " + se);
        // }
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.quipoz.framework.util.AppVars#set(com.quipoz.framework.util.AppVars)
     *      This override also copies any local variable values
     *      <p>
     *      Exceptions: externals and arrays; these are locally used only
     */
    @Override
    public void set(final AppVars av) {
        super.set(av);

        if (this != av) {

            COBOLAppVars lav = (COBOLAppVars) av;

            for (int i = 0; i < 100; i++) {
                IndicArea[i].set(lav.IndicArea[i].isOn());
            }

            indh1.set(lav.indh1);
            indlr.set(lav.indlr);
            indka.set(lav.indka);
            indkb.set(lav.indkb);
            indkc.set(lav.indkc);
            indkd.set(lav.indkd);
            indke.set(lav.indke);
            indkf.set(lav.indkf);
            indkg.set(lav.indkg);
            indkh.set(lav.indkh);
            indki.set(lav.indki);
            indkj.set(lav.indkj);
            indkk.set(lav.indkk);
            indkl.set(lav.indkl);
            indkm.set(lav.indkm);
            indkn.set(lav.indkn);
            indko.set(lav.indko);
            indkp.set(lav.indkp);
            indkq.set(lav.indkq);
            indkr.set(lav.indkr);
            indks.set(lav.indks);
            indkt.set(lav.indkt);
            indku.set(lav.indku);
            indkv.set(lav.indkv);
            indkw.set(lav.indkw);
            indkx.set(lav.indkx);
            indky.set(lav.indky);
            indkz.set(lav.indkz);

            indoa.set(lav.indoa);
            indob.set(lav.indob);
            indoc.set(lav.indoc);
            indod.set(lav.indod);
            indoe.set(lav.indoe);
            indof.set(lav.indof);
            indog.set(lav.indog);
            indov.set(lav.indov);

            eof = lav.eof;
            COBOLFileError = lav.COBOLFileError;
            recordFound = lav.recordFound;
            exactMatch = lav.exactMatch;

            date.set(lav.date);
            month.set(lav.month);
            year.set(lav.year);
            day.set(lav.day);
            uDate.set(lav.uDate);
            uMonth.set(lav.uMonth);
            uYear.set(lav.uYear);
            uDay.set(lav.uDay);
            dontReshowMessages = lav.dontReshowMessages;
            displayedMessages = lav.displayedMessages;
            monitoredMessages.clear();
            monitoredMessages.putAll(lav.monitoredMessages);
            fieldChangeIndicators.clear();
            fieldChangeIndicators.putAll(lav.fieldChangeIndicators);
            userProfile.clear();
            userProfile.putAll(lav.userProfile);
            libraryList = lav.libraryList;
            printerFiles = lav.printerFiles;

            screenInfds.set(lav.screenInfds);
            terminalId = lav.terminalId;
            additionalValidKeys = lav.additionalValidKeys;

            progds.pgmNm.set(lav.progds.pgmNm);
            progds.statusCd.set(lav.progds.statusCd);
            progds.prevStatus.set(lav.progds.prevStatus);
            progds.sourceStmtNo.set(lav.progds.sourceStmtNo);
            progds.routineNm.set(lav.progds.routineNm);
            progds.noOfParams.set(lav.progds.noOfParams);
            progds.pgmLibrary.set(lav.progds.pgmLibrary);
            progds.filler01.set(lav.progds.filler01);
            progds.filler02.set(lav.progds.filler02);
            progds.fileNm.set(lav.progds.fileNm);
            progds.si.set(lav.progds.si);
            progds.userProfile.set(lav.progds.userProfile);
            lav.progds.jobNo.set(lav.progds.jobNo);
            lav.progds.jobDt.set(lav.progds.jobDt);
            lav.progds.pgmDt.set(lav.progds.pgmDt);
            lav.progds.pgmTi.set(lav.progds.pgmTi);
        }

        /*
         * If we come in again on the same Thread, and this Thread's instance of
         * AppVars.getInstance() in the ThreadLocal has been zapped, the
         * transient fields need to be recreated.
         */
        // if (arrays == null) {
        // arrays = new Hashtable();
        // }
        // if (tableMetaData == null) {
        // tableMetaData = new Hashtable(500);
        // }
        // if (overriddenTables == null) {
        // overriddenTables = new Hashtable();
        // }
        // if (routines == null) {
        // routines = new HashMap();
        // }
        // if (dataarea == null) {
        // dataarea = new DataArea();
        // }
        // if (sharedScreenVars == null) {
        // sharedScreenVars = new ISeriesScreenVarsSharedStore();
        // }
        // if (temporaryTables == null) {
        // temporaryTables = new StringArrayList();
        // }
        recreateTransientFields();
    }

    /**
     * Set the position of the array to the passed element.
     * 
     * @param anArray
     *            - array to be searched and positioned.
     * @param pos
     *            - element number to set.
     */
    public void setCurrentIndexOf(final Object[] anArray, final BaseData pos) {
        setCurrentIndexOf(anArray, pos.toInt());
    }

    /**
     * Set the position of the array to the passed element. Set an indicator on
     * if no such element exists.
     * 
     * @param anArray
     *            - array to be searched and positioned.
     * @param pos
     *            - element number to set.
     * @param err
     *            - indicator to set if error.
     */
    public void setCurrentIndexOf(final Object[] anArray, final BaseData pos, final Indicator err) {
        try {
            setCurrentIndexOf(anArray, pos);
        } catch (Exception e) {
            err.setOn();
        }
    }

    /**
     * Supports the RPG concept of a positioned array. An array has a conceptual
     * position, which is set only by the lookup function. Subsequent references
     * to the array without any element qualification mean the element that was
     * found.
     * <p>
     * Note that this method is expected to be called from RPGFunctions only.
     * 
     * @param anArray
     * @param pos
     *            - the current position
     */
    public void setCurrentIndexOf(final Object[] anArray, final int pos) {
        if (arrays.get(anArray + "") != null) {
            ((IntegerData) arrays.get(anArray + "")).set(pos);
        } else {
            arrays.put(anArray + "", new IntegerData(pos));
        }
    }

    /**
     * Supports the RPG concept of a positioned array. An array has a conceptual
     * position, which may be set via an occur clause. Subsequent references to
     * the array without any element qualification mean the element referenced
     * by the passed variable.
     * <p>
     * The code searches the internal list of arrays that are known about, finds
     * any that are correlated, and makes sure that all such references get the
     * new locator/
     * 
     * @param anArray
     * @param occur
     *            - the locator
     */
    public void setCurrentLocatorOf(final Object anArray, final IntegerData occur) {
        Object o = getCurrentLocatorOf(anArray);
        Object k = null;
        Object v = null;
        Enumeration en = arrays.keys();
        while (en.hasMoreElements()) {
            k = en.nextElement();
            v = arrays.get(k);
            if (v == o && v != occur) {
                arrays.put(k, occur);
            }
        }
    }

    /**
     * Logs a Screen Field as one that will set or clear an indicator depending
     * on whether or not the user changes it. See {@link #fieldChangeIndicators}
     * .
     * 
     * @param field
     *            - BaseScreenData detailing the field concerned. If a String,
     *            the name of the field, case sensitive. Leading and trailing
     *            blanks will be removed. If a BaseScreenData, the variable will
     *            be accessed to extract the field name.
     * @param ind
     *            - the RPG indicator to set. Any indicator can be used.
     */
    public void setFieldChange(final BaseScreenData field, final Indicator ind) {
        /*
         * If a field has been set, then it indicates that it should be a
         * subfile field.
         */
        if (fieldChangeIndicators.containsKey(field.getFieldName())) {
            setSubfileFieldChange(field, ind);
        } else {
            fieldChangeIndicators.put(field.getFieldName(), new Object[] { field.toString(), ind });
        }
    }

    /**
     * Logs a Screen Field as one that will set or clear an indicator depending
     * on whether or not the user changes it. See {@link #fieldChangeIndicators}
     * .
     * 
     * @param field
     *            - BaseScreenData detailing the field concerned. If a String,
     *            the name of the field, case sensitive. Leading and trailing
     *            blanks will be removed. If a BaseScreenData, the variable will
     *            be accessed to extract the field name.
     * @param ind
     *            - the RPG indicator to set, as an int. Special indicators
     *            cannot be used.
     */
    public void setFieldChange(final BaseScreenData field, final int ind) {
        setFieldChange(field, IndicArea[ind]);
    }

    public void setHibernateSession(Session session) {
        this.hibernateSession = session;
    }

    /**
     * Sets an indicator to the passed value.
     * 
     * @param ind
     * @param value
     */
    public void setInd(final BaseData ind, final BaseData value) {
        setInd(ind.toInt(), value.toBoolean());
    }

    /**
     * Sets an indicator to the passed value.
     * 
     * @param ind
     * @param value
     */
    public void setInd(final BaseData ind, final boolean value) {
        setInd(ind.toInt(), value);
    }

    /**
     * Sets an indicator to the passed value.
     * 
     * @param ind
     * @param value
     */
    public void setInd(final int ia, final BaseData value) {
        setInd(ia, value.toBoolean());
    }

    /**
     * Sets an indicator to the passed value.
     * 
     * @param ind
     * @param value
     */
    public void setInd(final int ia, final boolean value) {
        if (ia < 0 || ia > 99) {
            return;
        }
        IndicArea[ia].set(value);
    }

    /**
     * Sets indicators to saved values starting at a given number.
     * 
     * @param fromInd
     * @param String
     */
    public void setInds(final BaseData arr) {
        setInds(0, arr);
    }

    /**
     * Sets indicators to saved values starting at a given number.
     * 
     * @param start
     *            - starting indicator number
     * @param arr
     *            - converted to String, sets values based on '1' = true,
     *            anything else = false
     */
    public void setInds(final BaseData start, final Object arr) {
        setInds(start.toInt(), arr);
    }

    /**
     * Sets indicators to saved values starting at a given number.
     * 
     * @param start
     *            - starting indicator number
     * @param arr
     *            - converted to String, sets values based on '1' = true,
     *            anything else = false
     */
    public void setInds(final int start, final Object arr) {
        char[] inds = arr.toString().toCharArray();
        for (int i = 0; i < inds.length && i < IndicArea.length; i++) {
            IndicArea[i + start].set(inds[i] == '1');
        }
    }

    /**
     * Sets all indicators on or off.
     * 
     * @param s
     */
    public void setInds(final String s) {
        FixedLengthStringData fsd = new FixedLengthStringData(100);
        fsd.fill(s);
        setInds(0, fsd);
    }

    /**
     * Sets the JobInfo object primarily used for storing job information
     * 
     * @param jobInfo
     */
    public void setJobinfo(final JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    /**
     * Sets the number of rows processed in last sql statement.
     * 
     * @param numberOfRowsProcessed
     *            - number of rows processed
     */
    public void setNumberOfRowsProcessed(final BaseData numberOfRowsProcessed) {
        setNumberOfRowsProcessed(numberOfRowsProcessed.toInt());
    }

    /**
     * Sets the number of rows processed in last sql statement.
     * 
     * @param numberOfRowsProcessed
     *            - number of rows processed
     */
    public void setNumberOfRowsProcessed(final int numberOfRowsProcessed) {
        this.numberOfRowsProcessed = numberOfRowsProcessed;
    }

    /**
     * Sets the pass-through data
     * 
     * @param data
     *            The pass-through data
     * @param dataLength
     *            The length of the pass-through data
     */
    public void setPassthroughData(final FixedLengthStringData data, final BinaryData dataLength) {
        ptd = new FixedLengthStringData(dataLength.toInt());
        ptd.setsub1string(1, dataLength, data);
    }

    /**
     * Set a printer file into the HashMap containing all the printer files
     * 
     * @param printerFile
     *            hashmap containing printer file as a HashMap
     */
    public void setPrinterFileInMap(final HashMap printerFile, final String filename, final boolean preExisting) {
        if (preExisting == true) {
            printerFiles.remove(filename);
        }
        printerFiles.put(filename, printerFile);
    }

    /**
     * Set the printer files hashmap with the one passed
     * 
     * @param printerFile
     *            hashmap containing printer files
     */
    public void setPrinterFilesMap(final HashMap printerFiles) {
        printerFiles.clear();
        this.printerFiles = printerFiles;
    }

    protected void setProgramName(final String pgmName) {
        if (progds == null) {
            progds = new ISeriesProgStat();
        }
        progds.pgmNm.set(pgmName);
    }

    /**
     * Set the temporary file area connection
     * 
     * @param connection
     */
    public void setQtempConnection(final Connection connection) {
        qtempConnection = connection;
    }

    /**
     * Logs a Screen Record as one that will set or clear an indicator depending
     * on whether or not the user changes it. See {@link #fieldChangeIndicators}
     * .
     * 
     * @param fields
     *            - Array containing all the fields in the record.
     * @param ind
     *            - the RPG indicator to set. Any indicator can be used.
     */
    public void setRecChange(final Object[] fields, final Indicator ind) {
        String[] values = new String[fields.length];
        String names = "$Record";
        for (int i = 0; i < fields.length; i++) {
            names += "/" + ((BaseScreenData) fields[i]).getFieldName();
            values[i] = fields[i].toString();
        }
        fieldChangeIndicators.put(names, new Object[] { values, ind });
    }

    public void setRoutines(Map<Object, Object> newRoutines) {
        routines.clear();
        routines.putAll(newRoutines);
    }

    /**
     * Set SQL error code, which actually save the SQLException for later usage.
     * 
     * @param se
     *            - SQLException
     */
    public void setSqlErrorCode(final int errorCode) {
        this.sqlError = null;
        this.sqlCode = errorCode;
    }

    /**
     * Set SQL error code, which actually save the SQLException for later usage.
     * 
     * @param se
     *            - SQLException
     */
    public void setSqlErrorCode(final SQLException se) {
        this.sqlError = se;
        this.sqlCode = se == null ? 0 : se.getErrorCode();
    }

    /**
     * Save subfile fields.
     * 
     * @link @(BaseScreenData field, Indicator ind)
     * @param tm
     * @param field
     * @param ind
     */
    @SuppressWarnings("unchecked")
    public void setSubfileFieldChange(final BaseScreenData field, final Indicator ind) {
        String fieldName = field.getFieldName();
        List<Object[]> changeList;
        if (!fieldChangeIndicators.containsKey(fieldName)) {
            changeList = new ArrayList<Object[]>();
        } else {
            Object o = fieldChangeIndicators.get(fieldName);
            if (o instanceof List) {
                changeList = (ArrayList<Object[]>) o;
            } else {
                changeList = new ArrayList<Object[]>();
                changeList.add((Object[]) o);
            }
        }
        changeList.add(new Object[] { field.toString(), ind });
        fieldChangeIndicators.put(fieldName, changeList);
    }

    /**
     * This method is not used anywhere yet.
     * 
     * @param field
     * @param ind
     * @param rowNo
     *            --> used as a key
     */
    @SuppressWarnings("unchecked")
    public void setSubfileFieldChange(final BaseScreenData field, final Indicator ind, final int rowNo) {
        String fieldName = field.getFieldName();
        Map<String, Object[]> changeMap;
        if (!fieldChangeIndicators.containsKey(fieldName)) {
            changeMap = new LinkedHashMap<String, Object[]>();
        } else {
            Object o = fieldChangeIndicators.get(fieldName);
            if (o instanceof Map) {
                changeMap = (Map<String, Object[]>) o;
            } else {
                changeMap = new LinkedHashMap<String, Object[]>();
                if (rowNo > 0) {
                    changeMap.put(String.valueOf(rowNo - 1), (Object[]) o);
                } else {
                    throw new IllegalArgumentException("Row no 0 is specified, but field is already saved.");
                }
            }
        }
        changeMap.put(String.valueOf(rowNo), new Object[] { field.toString(), ind });
        fieldChangeIndicators.put(fieldName, changeMap);
    }

    /**
     * Logs a Screen Field as one that will be set to the row number of the
     * subfile that the cursor is positioned in. Emulates the iSeries DSPF
     * SFLCSRRRN verb.
     * 
     * @param toSet
     *            - the name of the field to set, in screenVars.
     */
    public void setSubfileRowField(final String toSet) {
        fieldChangeIndicators.put("SFLCSRRRN", new Object[] { toSet });
    }

    /**
     * @param terminalId
     *            the terminalId to set
     */
    public void setTerminalId(final String terminalId) {
        this.terminalId = terminalId;
    }

    /**
     * Set the value of the user profile, see userProfile definition.
     * 
     * @param profile
     *            Specifies the profile of a user
     * @param value
     *            Specifies the value related to the profile
     */
    public void setUserProfile(final String profile, final String value) {

    }

    /**
     * Sets up a request to set an RPG indicator if a valid command key is
     * pressed. A valid key is one that was listed in the DSPF.
     * <p>
     * Theoretically applicable at the Record level, this will be applied for
     * all valid command keys listed in the XML.
     * 
     * @param actions
     *            - the set of valid PF keys. It is expected this will have been
     *            obtained on the JSP via ScreenModel.getFormActions()
     * @param ind
     *            - the RPG indicator to set.
     */
    public void setValidCommands(final String[] actions, final int ind) {
        fieldChangeIndicators.put(DOLLARCMD, new Object[] { actions, IndicArea[ind] });
    }

    /**
     * implementation of STRCMTCTL; If txConnection is not null, then send
     * escape message - CPF8351; else create new connection for transaction
     * management, and set auto commit as flase; Max W
     */
    @Override
    public void startCommitControl() throws ExtMsgException {

        // txConnection is not null, means the transaction contorl has been
        // started
        if (txConnection != null) {
            // diff policy with life
            try {
                txConnection.close();
                txConnection = null;
                if (this.getHibernateSession() != null) {
                    this.getHibernateSession().close();
                    this.setHibernateSession(null);
                }
            } catch (Exception e) {
                LOGGER.error("startCommitControl()", e);
            }
            LOGGER.debug("Commitment control already active.");
            // addExtMessage("CPF8351", "Commitment control already active.");
            // end diff policy with life
        }
        // create new database connection for transaction control
        Connection conn = null;
        try {
            conn = getNewDBConnection();
            // conn.setAutoCommit(false);
            txConnection = conn;
            this.setHibernateSession(HibernateUtil.openSession(conn));
            this.getHibernateSession().beginTransaction();
            conn = null;
        } catch (SQLException e) {
            txConnection = null;
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    // the errror is ignored at the moment, but diagnositc might
                    // should be added here;
                    addDiagnostic(e1.getLocalizedMessage());
                }
            }
            // add escapte message
            String msg = "Commitment control operation failed.";
            addExtMessage("CPF9255", msg);
            LOGGER.error(msg + " Reason:\n", e);
        }
        return;
    }

}
