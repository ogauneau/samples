
# Non Object Oriented COBOL to Java interoperability

- Project **cics-to-java** is an example of calling Java from a CICS COBOL program
- Project **cobolbatch-to-java** is an example of calling Java from a COBOL Batch program
- Project **ims-to-java** is an example of calling Java from an IMS BMP COBOL program and also running a Java program in a JBP Region

## cics-to-java

![swim diagram](https://github.com/ogauneau/samples/blob/main/cics-to-java/cicshelloswim.jpg)

## cobolbatch-to-java

An example of a simple COBOL batch program calling a Java method using the cjbuild utility to generate the JNI interfaces.

![flow diagram](https://github.com/ogauneau/samples/blob/main/cobolbatch-to-java/c2jbatch.png)

## ims-to-java

JCL IMS4C2J.jcl 
- creates a GSAM database, a PSB and a COBOL program calling the Java method og.ims.samples.ExportIVPDB1.exportAllToConsole().
- build the COBOL program and generates its JNI interfaces using cjbuild
- declare the PSB in IMS and starts it
- run the COBOL program in a BMP region
(it actually does more than exporting to the System.out console as it can also export the IVP database IVPDB1 to my GSAM database).

![flow diagram](https://github.com/ogauneau/samples/blob/main/ims-to-java/flow1.png)

