# Dropwizard Crypto

A Dropwizard bundle that adds encryption support to an application's ObjectMapper.

[![Build Status](https://travis-ci.org/meltmedia/dropwizard-crypto.svg)](https://travis-ci.org/meltmedia/dropwizard-crypto)

## Usage

### Maven

This project releases to Maven Central.  To use the bundle, simply include its dependency in your project.

```
<dependency>
  <groupId>com.meltmedia.dropwizard</groupId>
  <artifactId>dropwizard-crypto</artifactId>
  <version>0.1.0</version>
</dependency>
```

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

### Java

Include the bundle in the `initialize` method of your application:

```
import com.meltmedia.dropwizard.crypto.CryptoBundle;

...

@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
  bootstrap.addBundle(
    CryptoBundle
      .builder().build());
}
```

And include the `@Encrypted` annotations on your Confituration object's encrypted properties:

```
@Encrypted
public String getSecret() {
  return secret;
}
```

### Environment

When running your application, define the passphrase in the environment.

```
export DROPWIZARD_PASSPHRASE='correct horse battery staple'
```

### Configuration

Create an unencrypted version of your configuration.

```
secret: secret
```

Then pass it through the encryption command.

```
dropwizard-app encrypt -p /secret unencrypted.yml encrypted.yml
```

This will give you an encrypted version of your config.

```
---
secret:
  salt: "tKD8wQ=="
  iv: "s9hTJRaZn6fxxpA4nVfDag=="
  value: "UZENJOltf+9EZS03AXbmeg=="
  cipher: "aes-256-cbc"
  keyDerivation: "pbkdf2"
  keyLength: 256
  iterations: 2000
  encrypted: true
```

### Running

Run any of your application's configured commands like you normally would, just pass in a version of the configuration with encrypted values.

```
dropwizard-app server encrypted.yml
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
