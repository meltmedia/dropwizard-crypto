# Dropwizard Crypto

This project provides a Dropwizard Bundle that provides crytpographic utilities to an applications ObjectMapper.

[![Build Status](https://travis-ci.org/meltmedia/dropwizard-crypto.svg)](https://travis-ci.org/meltmedia/dropwizard-crypto)

## Usage

### Maven

To use SNAPSHOTs of this project, you will need to include the sonatype repository in your POM.

```
<repositories>
    <repository>
        <snapshots>
        <enabled>true</enabled>
        </snapshots>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

You will also need to include the project in your dependencies.

```
<dependency>
  <groupId>com.meltmedia.dropwizard</groupId>
  <artifactId>dropwizard-crypto</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Java

```
import com.meltmedia.dropwizard.crypto.CryptoBundle;

...

@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
  bootstrap.addBundle(
    CryptoBundle.builder().build());
}
```

### Environment

```
export DROPWIZARD_PASSPHRASE='A super secret phrase.'
```

## Building

This project builds with Java8 and Maven 3.  Simply clone the repo and run

```
mvn clean install
```

from the root directory.

## Contributing

This project accepts PRs, so feel free to fork the project and send contributions back.

### Formatting

This project contains formatters to help keep the code base consistent.  The formatter will update Java source files and add headers to other files.  When running the formatter, I suggest the following procedure:

1. Make sure any outstanding stages are staged.  This will prevent the formatter from destroying your code.
2. Run `mvn format`, this will format the source and add any missing license headers.
3. If the changes look good and the project still compiles, add the formatting changes to your staged code.

If things go wrong, you can run `git checkout -- .` to drop the formatting changes. 
