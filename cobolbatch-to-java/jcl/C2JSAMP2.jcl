//C2JSAMP JOB ,
// MSGCLASS=H,REGION=0M
//*************************************************************
//*
//* Created by Olivier Gauneau
//*
//*  JCL to compile, and run
//*  a 31 bit COBOL program (DD CBLSRC) calling a
//*  static 64 bit Java method (DD JAVASRC)
//*
//*  Java output is written to DD STDOUT
//*  COBOL output is written to DD SYSOUT
//*
//* note:
//*
//* JCL variables (&VAR.) are substituted in STDENV DD
//* so that STDPARM DD is similar to a USS shell script
//* and uses env variables ($VAR.)
//*************************************************************
//        EXPORT SYMLIST=*
// SET COBPRFX='IGY.V6R4M0'
// SET LIBPRFX='CEE'
// SET JAVAHOME='/usr/lpp/java/J11.0_64'
// SET COBPDS='GAUNEAU.SRC.COBOL'
// SET LOADLIB='BUILDER.TEST.PDSE.LOAD'
// SET WORKDIR='/u/gauneau/tmp/c2j'
//*
//*************************************************************
//* Create USS env
//* Java classes created in subdirectory/package test
//*************************************************************
//*
//MKUSS  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
rm -rf $WORK_DIR;
mkdir $WORK_DIR;
cd $WORK_DIR;
mkdir src;
mkdir src/test;
mkdir src/test/generated;
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
//* Create the COBOL source file
//* for Java/COBOL mapping, see
//* https://www.ibm.com/docs/en/cobol-zos/6.4?topic=
//* ci-mapping-between-cobol-java-data-types-non-oo-coboljava-
//* interoperability#rldirmap__typemap
//*************************************************************
//*
//MKCOB EXEC PGM=IKJEFT01,COND=(4,LT)
//USS        DD PATH='&WORKDIR./src/COBPROG.cbl',
//             PATHOPTS=(OWRONLY,OCREAT),
//             PATHMODE=(SIRWXU,SIRGRP,SIXGRP,SIROTH,SIXOTH)
//SYSTSPRT DD SYSOUT=*
//SYSTSIN  DD *
OCOPY INDD(CBLSRC) OUTDD(USS) TEXT CONVERT(YES) PATHOPTS(USE)
/*
//CBLSRC   DD *
       CBL PGMNAME(LONGMIXED)
      *
      *
       IDENTIFICATION DIVISION.
       PROGRAM-ID. 'COBPROG'.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 ARGS.
          05 ARG1 PIC X(200).
          05 ARG2 PIC S9(9) COMP-5.
       >>JAVA-SHAREABLE ON
       01 ARG3 PIC X(200).
       >>JAVA-SHAREABLE OFF

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

           MOVE ALL 'X' TO ARG1
           MOVE 123 TO ARG2
           MOVE ALL 'Value3'TO ARG3.

           CALL 'Java.test.JavaProg.doSomething' USING ARG1, ARG2
                RETURNING JAVA-MESSAGE
           ON EXCEPTION
              DISPLAY "Java Exception occurred in HelloWorld"
              GOBACK
           END-CALL
           DISPLAY "Java returned:" JAVA-MESSAGE
           DISPLAY "Arg3 is now " ARG3
           DISPLAY 'COBOL ended'
           GOBACK.
/*
//*
//*************************************************************
//* Create methods file containing the Java methods called
//*  by COBOL
//* This file is used by cjbuild in step CJBLD
//*************************************************************
//*
//MKMTHD  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
cd $WORK_DIR;

echo COBPROG > methods;
echo Java.test.JavaProg.doSomething >> methods;
echo Java.com.ibm.jzos.ZUtil.redirectStandardStreams >> methods;

/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
WORK_DIR=&WORKDIR.
//*
//*************************************************************
//* Create the Java source file
//*************************************************************
//*
//MKJAVA EXEC PGM=IKJEFT01,COND=(4,LT)
//USS        DD PATH='&WORKDIR./src/test/JavaProg.java',
//             PATHOPTS=(OWRONLY,OCREAT),
//             PATHMODE=(SIRWXU,SIRGRP,SIXGRP,SIROTH,SIXOTH)
//SYSTSPRT DD SYSOUT=*
//SYSTSIN  DD *
OCOPY INDD(JAVASRC) OUTDD(USS) TEXT CONVERT(YES) PATHOPTS(USE)
/*
//JAVASRC        DD *
package test;

public class JavaProg {

  public static String doSomething(String arg1,int arg2) {
    System.out.println("arg1="+arg1);
    System.out.println("arg2="+arg2);
    String arg3 = test.generated.strg.COBPROG.ARG3.get();
    System.out.println("arg3="+arg3);
    StringBuilder buf3 = new StringBuilder(arg3);
    buf3.setCharAt(0,'Z');
    buf3.setCharAt(199,'Z');
    arg3=buf3.toString();
    test.generated.strg.COBPROG.ARG3.put(arg3);
    return "All good!";
  }
}
/*
//*
//*************************************************************
//* compile COBOL program (see COBPROG.lst)
//*  and generate Java IOP
//*************************************************************
//*
//BLDCOB  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
export PATH=$PATH:$COBPATH;
echo $PATH;
cd $WORK_DIR;
cob2 -c ./src/COBPROG.cbl \
 "-qjavaiop(OUTPATH('$WORK_DIR/javaiop'),JAVA64)";
/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
WORK_DIR=&WORKDIR.
COBPATH=/u/sandbox/usr/cobol/V6R4M0/bin
STEPLIB=IGY.V6R4M0.SIGYCOMP
/*
//*
//*************************************************************
//* call CJBuild using MIX_31_64 to
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
cd $WORK_DIR;
cjbuild -v  -p test.generated
  -m MIX_31_64
  -c $WORK_DIR/javaiop
  -d "//'$LOADLIB'"
  -s $WORK_DIR/src/test/generated -j $WORK_DIR/class methods c2ja;
/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
WORK_DIR=&WORKDIR.
LOADLIB=&LOADLIB.
COBPATH=/u/sandbox/usr/cobol/V6R4M0/bin
STEPLIB=IGY.V6R4M0.SIGYCOMP
JAVA_HOME=&JAVAHOME
/*
//*
//*************************************************************
//* Build Java file
//*************************************************************
//*
//BLDJAVA EXEC PGM=BPXBATSL,COND=(4,LT)
//STDPARM DD *,SYMBOLS=EXECSYS
PGM &JAVAHOME/bin/javac
 -cp &WORKDIR./class
 -d &WORKDIR./class
 &WORKDIR./src/test/JavaProg.java
/*
//STDOUT  DD SYSOUT=*
//STDERR  DD SYSOUT=*
//STDENV  DD *,SYMBOLS=EXECSYS
JAVA_HOME=&JAVAHOME
/*
//*
//*************************************************************
//* Link them all
//* - COBPROG.o
//* - Java and COBOL IOP programs in LIBC2JA
//*************************************************************
//*
//LINK  EXEC PGM=BPXBATCH,COND=(4,LT)
//STDPARM  DD *,SYMBOLS=EXECSYS
SH echo WORK_DIR=$WORK_DIR;
export PATH=$PATH:$COBPATH:$JAVA_HOME/bin;
echo $PATH;
cd $WORK_DIR;
cob2 ./COBPROG.o -o "//'$LOADLIB(COBPROG)'"
 ./libc2ja.x;

/*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *,SYMBOLS=JCLONLY
LOADLIB=&LOADLIB.
WORK_DIR=&WORKDIR.
COBPATH=/u/sandbox/usr/cobol/V6R4M0/bin
STEPLIB=IGY.V6R4M0.SIGYCOMP
/*
//*
//*************************************************************
//* Run it
//*************************************************************
//*
//GO EXEC PGM=COBPROG,COND=(4,LT)
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