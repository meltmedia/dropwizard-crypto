# Example

This example shows how to use the Dropwizard Encryption bundle to secure sensitive information in your configuration.

## Before You Begin

- Make sure you have build the project using the instructions in the parent project.  
- These instructions assume that the example project is your current working directory.

## Setting the Passphrase

Before running any of the commands on the dropwizard application, you are going to need to define the encryption passphrase to use.  To set the passphrase, use the following command:

```
export DROPWIZARD_PASSPHRASE='correct horse battery staple'
```
Make sure to do this in each terminal that you use.

## Decrypting Configuration Values

You can decrypt properties in the configuration file using the `decrypt` command.  It takes a JSON Pointer to the element to decrypt, a source (defaults to STDIN) and a target (defaults to STDOUT).  You can decrypt the secret in the provided `example.json` configuration with this command:

```
./target/dropwizard-crypto decrypt -p /secret config/example.yml
```

## Encrypting Configuration Values

To encrypt a configuration value, create a YAML document with the unencrypted value and then pass it through the `encrypt` command.  You can then use the output with the example application.

```
echo "secret: shh" | ./target/dropwizard-crypto encrypt -p /secret
```
