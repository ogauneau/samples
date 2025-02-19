//C2J#SAMP JOB ,
// MSGCLASS=H,REGION=0M
//*************************************************************
//*
//*  JCL to compile, and run
//*  an COBOL program (DD CBLSRC) calling a
//*  static Java method (DD JAVASRC)
//*
//*  Java output is written to DD STDOUT
//*  COBOL output is written to DD SYSOUT
//*
//* note:
//*
//* JCL variables (&VAR.) are substituted in STDENV DD
//* so that STDPARM DD is similar to a USS shell script
//* and uses env variables ($VAR.)
//*
//* tested with COBOL compiler v6.4.0 P241211 and Java v17
//*
//*************************************************************
//*
//        EXPORT SYMLIST=*
// SET COBPRFX='IGY'
// SET COBPATH='/usr/lpp/IBM/cobol/igyv6r4'
// SET LIBPRFX='CEE'
// SET JAVAHOME='/usr/lpp/java/J17.0_64'
// SET COBPDS='OLIVIER.SRC.COBOL'
// SET LOADLIB='OLIVIER.BUILD.PDSE.LOAD'
// SET OBJLIB='OLIVIER.BUILD.OBJ'
// SET WORKDIR='/u/olivier/tmp/c2j'
//*
//*************************************************************
//* Create USS env
//*************************************************************
//*
//MKUSS  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
rm -rf $WORK_DIR;
mkdir $WORK_DIR;
cd $WORK_DIR;
mkdir src;
mkdir javaiop;
mkdir class;
echo USS env created
/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
WORK_DIR=&WORKDIR.
/*
//*
//*************************************************************
//* Create the Java source file
//*************************************************************
//*
//MKJAVA EXEC PGM=IKJEFT01,COND=(4,LT)
//USS        DD PATH='&WORKDIR./src/HelloWorld.java',
//             PATHOPTS=(OWRONLY,OCREAT),
//             PATHMODE=(SIRWXU,SIRGRP,SIXGRP,SIROTH,SIXOTH)
//SYSTSPRT DD SYSOUT=*
//SYSTSIN  DD *
OCOPY INDD(JAVASRC) OUTDD(USS) TEXT CONVERT(YES) PATHOPTS(USE)
/*
//JAVASRC        DD *
package test;

public class HelloWorld {

  public static String sayHelloTo(String name) {
    String message = "Hello "+name+" and welcome to Java!";
    System.out.println("Java says: "+message);
    return "All good!";
  }
}
/*
//*
//*************************************************************
//* Build Java file
//*************************************************************
//*
//BLDJAVA EXEC PGM=BPXBATSL,COND=(4,LT)
//STDPARM DD *,SYMBOLS=EXECSYS
PGM &JAVAHOME/bin/javac
 -d &WORKDIR./class
 &WORKDIR./src/HelloWorld.java
/*
//STDOUT  DD SYSOUT=*
//STDERR  DD SYSOUT=*
//STDENV  DD *,SYMBOLS=EXECSYS
JAVA_HOME=&JAVAHOME
/*
//*
//*************************************************************
//* compile COBOL program (see C2J#HELO.lst)
//*  and generate Java IOP
//*************************************************************
//*
//BLDCOB    EXEC PGM=IGYCRCTL,COND=(4,LT),REGION=0M,
//            PARM=('PGMNAME(LONGMIXED)',
//         'JAVAIOP(OUTPATH(''&WORKDIR./javaiop''),JAVA64)')
//STEPLIB  DD DISP=SHR,DSN=&COBPRFX..SIGYCOMP
//SYSPRINT DD SYSOUT=*
//SYSMDECK DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT1   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT2   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT3   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT4   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT5   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT6   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT7   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT8   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT9   DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT10  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT11  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT12  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT13  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT14  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSUT15  DD UNIT=SYSALLDA,SPACE=(CYL,(1,1))
//SYSXMLSD DD DUMMY
//COBOL.SYSLIN DD DISP=SHR,
//        DSN=&OBJLIB.(C2JHELO)
//COBOL.SYSIN DD *
      *
      *
       IDENTIFICATION DIVISION.
       PROGRAM-ID. 'C2JHELO'.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 USER-NAME     PIC X(10) VALUE "Olivier".
       01 JAVA-MESSAGE  PIC X(50) VALUE SPACE.

       PROCEDURE DIVISION.
       MAINPROGRAM.
      *
           DISPLAY 'COBOL started, test1'
      * redirect Java stdout/stderr to DDs STDOUT/STDERR
           CALL 'Java.com.ibm.jzos.ZUtil.redirectStandardStreams'
           ON EXCEPTION
              DISPLAY "Java Exception occurred in ZUtil"
              GOBACK
           END-CALL

           CALL 'Java.test.HelloWorld.sayHelloTo' USING USER-NAME
                RETURNING JAVA-MESSAGE
           ON EXCEPTION
              DISPLAY "Java Exception occurred in HelloWorld"
              GOBACK
           END-CALL
           DISPLAY "Java returned:" JAVA-MESSAGE
           DISPLAY 'COBOL ended'
           GOBACK.
/*
//*
//*************************************************************
//* call CJBuild to
//*    - generate COBOL IOP (IGYCJEST and IGYCJIMC)
//*    - compile Java and COBOL IOP programs
//*    - generate a DLL import file (libc2ja.x)
//*    - link it all into BUILDER.TEST.PDSE.LOAD(LIBC2JA)
//*************************************************************
//*
//CJBLD  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
export PATH=$PATH:$COBPATH:$JAVA_HOME/bin;
echo $PATH;
cd $WORK_DIR/javaiop;
cjbuild -v -p com.ibm.zdevops
  -m MIX_31_64
  -c $WORK_DIR/javaiop
  -d "//'$LOADLIB'"
  -s $WORK_DIR/src -j $WORK_DIR/class c2jb1;
/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
WORK_DIR=&WORKDIR.
LOADLIB=&LOADLIB.
COBPATH=&COBPATH./bin
STEPLIB=&COBPRFX..SIGYCOMP
JAVA_HOME=&JAVAHOME
/*
//*
//*************************************************************
//* Link them all
//* - C2J#HELO.o
//* - Java and COBOL IOP programs in LIBC2JA
//*************************************************************
//*
//LINK  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
export PATH=$PATH:$COBPATH:$JAVA_HOME/bin;
echo $PATH;
cd $WORK_DIR/javaiop;
cob2 -v -bLIST,MAP,XREF "//'$OBJLIB(C2JHELO)'"
 -o "//'$LOADLIB(C2JHELO)'" ./libc2jb1.x;

/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
LOADLIB=&LOADLIB.
OBJLIB=&OBJLIB.
WORK_DIR=&WORKDIR.
COBPATH=&COBPATH./bin
STEPLIB=&COBPRFX..SIGYCOMP
/*
//*
//*************************************************************
//* Run it
//*************************************************************
//*
//GO EXEC PGM=C2JHELO,COND=(4,LT)
//STDENV DD *,SYMBOLS=JCLONLY
_CEE_ENVFILE_COMMENT=#
_CEE_ENVFILE_CONTINUATION=\
# lib:/usr/lib
LIBPATH=&JAVAHOME./lib/j9vm\
:&JAVAHOME./lib/
PATH=bin:&JAVAHOME./bin
CLASSPATH=&WORKDIR./class
COBJVMINITOPTIONS=-XX:+Enable3164Interoperability
/*
//CEEOPTS DD *
* Be careful when editing: quoted ENVARS wrap at col 72
ENVAR(_CEE_ENVFILE_S=DD:STDENV)
POSIX(ON)
*
//STEPLIB DD DSN=&LOADLIB.,DISP=SHR
// DD DSN=&LIBPRFX..SCEERUN2,DISP=SHR
// DD DSN=&LIBPRFX..SCEERUN,DISP=SHR
// DD DSN=&COBPRFX..SIGYCOMP,DISP=SHR
//SYSOUT DD SYSOUT=*
//CEEDUMP DD SYSOUT=*
//SYSUDUMP DD DUMMY
//*
//* ZUtil.redirectStandardStreams will point to these for sdtout/stderr
//*  Unless you add the -Djzos.merge.sysout=true option above.
//*  Using that option, both Java stdout/stderr with go to DD:SYSOUT
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//*
