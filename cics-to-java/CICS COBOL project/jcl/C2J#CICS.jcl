//OGCOBHW2 JOB  COND=(8,LT)
//*
//*************************************************
//*            build
//*************************************************
//*
//*
//********************************************
//* Deploy it to CICS
//********************************************
//*
//DEFINE   EXEC PGM=DFHCSDUP,REGION=0M,
//            PARM='CSD(READWRITE),PAGESIZE(60),NOCOMPAT'
//STEPLIB  DD DISP=SHR,DSN=DFH.V6R1M0.CICS.SDFHLOAD
//DFHCSD   DD DISP=SHR,DSN=CICS61.CICS01.DFHCSD
//SYSPRINT DD SYSOUT=*
//SYSIN     DD   *
*
* HELLO CICS to Java
*
DEFINE PROGRAM(OGCOBHW2) GROUP(OLIVIER)
        LANGUAGE(COBOL) DATALOCATION(ANY) EXECKEY(USER)

DEFINE TRANSACTION(OGHW) GROUP(OLIVIER)
    PROGRAM(OGCOBHW2)
    TASKDATALOC(ANY) TASKDATAKEY(USER)

/*