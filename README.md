# Keycloak WebAuthn Authenticator

[![Build Status](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator.svg?branch=master)](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator)
[![license](https://img.shields.io/github/license/webauthn4j/keycloak-webauthn-authenticator.svg)](https://github.com/webauthn4j/keycloak-webauthn-authenticator/blob/master/LICENSE)

[Web Authentication](https://www.w3.org/TR/webauthn/)(WebAuthn) sample plugin for [Keycloak](https://www.keycloak.org) , implements with [webauthn4j](https://github.com/webauthn4j/webauthn4j).

This sample plugin is developed in order to implement features defined in [the design document for WebAuthn support onto keycloak](https://github.com/keycloak/keycloak-community/blob/master/design/web-authn-authenticator.md), clarify issues for realizing these features and give feedback onto this design document.


## Important Notice

If you use the previous commits or versions, please first undeploy it, and after that, deploy the ear of the current version or commit.

  - `$ mvn wildfly:undeploy`
  
  - `$ mvn clean install wildfly:deploy`

If not undeploy the existing ear, an error occurs. This is because the current version removed RegisterAuthenticator implemented as Authenticator considering [the issue](https://github.com/webauthn4j/keycloak-webauthn-authenticator/issues/17).

## Environment

We've confirmed that this demo had worked well under the following environments:

- 2 Factor Authentication with Resident Key Not supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Google Chrome (ver 73), Mozilla FireFox (ver 66)
  - Authenticator : Yubico Security Key
  - Server(RP) : keycloak-5.0.0 on localhost

- 2 Factor Authentication with Resident Key Not supported Authenticator Scenario

  - OS : macOS Mojave (ver 10.14.3)
  - Browser : Google Chrome (ver 73), Mozilla FireFox (ver 66)
  - Authenticator : Yubico Security Key
  - Server(RP) : keycloak-5.0.0 on localhost

- 2 Factor Authentication with Resident Key supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Microsoft Edge (ver 44)
  - Authenticator : Internal Fingerprint Authentication Device
  - Server(RP) : keycloak-5.0.0 on localhost

- 2 Factor Authentication with Resident Key supported Authenticator Scenario

  - OS : macOS Mojave (ver 10.14.4)
  - Browser : Google Chrome (ver 75)
  - Authenticator : Touch ID
  - Server(RP) : keycloak-6.0.1 on localhost

- Authentication with Resident Key supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Microsoft Edge (ver 44)
  - Authenticator : Internal Fingerprint Authentication Device
  - Server(RP) : keycloak-5.0.0 on localhost

- Authentication with Resident Key supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Microsoft Edge (ver 44)
  - Authenticator : Yubico Security Key
  - Server(RP) : keycloak-5.0.0 on localhost


## Install

- Build:

  - `$ mvn install`

- Add the EAR file to the Keycloak Server:

  - `$ cp webauthn4j-ear/target/keycloak-webauthn4j-ear-*.ear $KEYCLOAK_HOME/standalone/deployments/`

- Or deploy the EAR file dynamically when the Keycloak Server:

  - `$ mvn clean install wildfly:deploy`

- Report coverage

  - `$ mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent test`
  - `$ mvn org.jacoco:jacoco-maven-plugin:report`

## Overview

This prototype consists of two components:

- WebAuthn Register

This enable users to register their accounts on keycloak with their authenticators' generating public key credentials. It is implemented as `Required Action`.

- WebAuthn Authenticator

This enable users to authenticate themselves on keycloak by their authenticators. It is implemented as `Authenticaor`.

## Realm Settings

To enable user without their accounts on keycloak to register them on the authentication flow:

- Enable `User registration` in 'Realm Settings' - 'Login'

## Authentication Required Actions Settings

To enable users to register their accounts with their authenticators' creating public key credentials:

-  register `Webauthn Register` Required Action in 'Required Actons' - 'Register'

-  check `Enabled` and `Default Action` for registered `Webauthn Register` Required Action


## Authentication Flow Settings

To enable users having their accounts on keycloak to authenticate themselves on keycloak by their authenticators:

### Browser Flow (2 Factor Authentication)

| Auth Type                    |                        | Requirement |
| ---------------------------- | ---------------------- | ----------- |
| Cookie                       |                        | ALTERNATIVE |
| Kerberos                     |                        | DISABLED    |
| Identity Provider Redirector |                        | ALTERNATIVE |
| Copy of Browser Forms        |                        | ALTERNATIVE |
|                              | Username Password Form | REQUIRED    |
|                              | OTP Form               | OPTIONAL    |
|                              | WebAuthn Authenticator | REQUIRED    |

### Browser Flow (Use `Resident Key`)

| Auth Type                    |     | Requirement |
| ---------------------------- | --- | ----------- |
| Cookie                       |     | ALTERNATIVE |
| Kerberos                     |     | DISABLED    |
| Identity Provider Redirector |     | ALTERNATIVE |
| WebAuthn Authenticator       |     | REQUIRED    |

## Notes

### User Registration in Authentication with Resident Key supported Authenticator Scenario

Browser Flow (Use `Resident Key`) automatically asks users to authenticate on their authenticators. Therefore, the users without their accounts have no chance to register them on this flow.

For such the users to register their accounts, please use the default Browser Flow. It is helpful to user `Authentication Flow Overrides` on Client Settings. You can set the default Browser Flow for User Account Service (Client ID: account) to let users register their accounts at first.

### Requiring Resident Key in Registration

On registration, the browser asks you if you would like to store ID and its credential on your authenticator(namely, Resident Key). If you push OK button, the browser tell your authenticator to do so explicitly. If not ,whether ID and its credential is Resident Key or not depends on authenticators.

Please note the followings:


- In Authentication with Resident Key supported Authenticator Scenario, only user's ID and its credential as Resident Key can be valid. Therefore, if you register ID and its credential that is not as Resident Key and try to authenticate with them, you fail to authenticate.


- Not all authenticators are capable of this Resident Key. The Authenticator lack of Resident Key capability fails to register user's ID and its credential when Resident Key is required explicitly.


- Not all browsers support this Resident Key. At least, I've confirmed that Microsoft Edge (ver.44) supports Resident Key.


## TODO

- [x] [credential storage : avoid creating a new table for credentials](https://github.com/webauthn4j/keycloak-webauthn-authenticator/issues/7)
- [x] [webauthn4j 0.9.2.RELEASE support](https://github.com/webauthn4j/keycloak-webauthn-authenticator/issues/8)
- [x] [Unit Test](https://github.com/webauthn4j/keycloak-webauthn-authenticator/issues/13)
- [ ] CI Integration

_TBD_
