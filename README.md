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

  - OS : Windows 10 (v1903)
  - Browser : Google Chrome (ver 73), Mozilla FireFox (ver 66, 68)
  - Authenticator : Yubico Security Key NFC (5.1.2), Yubikey 5C Nano
  - Server(RP) : keycloak-5.0.0, 6.0.1 on localhost

- 2 Factor Authentication with Resident Key Not supported Authenticator Scenario

  - OS : macOS Mojave (ver 10.14.3)
  - Browser : Google Chrome (ver 73), Mozilla FireFox (ver 66)
  - Authenticator : Yubico Security Key NFC (5.1.2)
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
  - Authenticator : Yubico Security Key NFC (5.1.2)
  - Server(RP) : keycloak-5.0.0 on localhost


## Install

- Build:

  - `$ mvn install`

- Add the EAR file to the Keycloak Server:

  - `$ cp webauthn4j-ear/target/keycloak-webauthn4j-ear-*.ear $KEYCLOAK_HOME/standalone/deployments/`

- Or deploy the EAR file dynamically when the Keycloak Server is running:

  - `$ mvn clean install wildfly:deploy`

- Report coverage

  - `$ mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent test`
  - `$ mvn org.jacoco:jacoco-maven-plugin:report`

## Overview

This prototype consists of two components:

- WebAuthn Register

This enables users to register their accounts on keycloak with their authenticators' generating public key credentials. It is implemented as `Required Action`.

- WebAuthn Authenticator

This enables users to authenticate themselves on keycloak by their authenticators. It is implemented as `Authenticator`.

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


## Authenticator Management

The user can only register their own authenticator. The user and the administrator can manage the registered authenticator. For the user to do so, the administrator set `Realm Settings -> Themes -> Account Theme` to "webauthn".

### User Editable Label for Registered Authenticator

As the metadata of the authenticator, the user can put the editable label onto their authenticator to identify it when registering it.

The user and the administrator can edit this label. 

If the user wants to edit this label, please access to [User Account Service](https://www.keycloak.org/docs/latest/server_admin/index.html#_account-service) and edit `Public Key Credential Label`.

If the administrator wants to edit some user's registered authenticator's label, please access to `Users -> (User Name) -> Attributes` and edit `public_key_credential_label`.


### AAGUID for Registered Authenticator

As the metadata of the authenticator, its AAGUID is stored onto keycloak when registering it.

The user and the administrator can view this AAGUID.

If the user wants to view this AAGUID, please access to [User Account Service](https://www.keycloak.org/docs/latest/server_admin/index.html#_account-service) and look up `Public Key Credential AAGUID`.

If the administrator wants to view some user's registered authenticator's label, please access to `Users -> (User Name) -> Attributes` and look up `public_key_credential_aaguid`.


### Delete Registered Authenticator

The user and the administrator can delete the registered authenticator.

If the user wants to delete its own regestered authenticator, please access to [User Account Service](https://www.keycloak.org/docs/latest/server_admin/index.html#_account-service) and clean up `Public Key Credential ID`, `Public Key Credential Label` and `Public Key Credential AAGUID`.

If the administrator wants to delete some user's registered authenticator, please access to `Users -> (User Name) -> Attributes` and delete `public_key_credential_id`, `public_key_credential_label` and `public_key_credential_aaguid`.


## Re-Register Authenticator

The user can re-register the authenticator. 

- The administrator goes to `Users -> (User Name) -> Details` and add `WebAuthn Register` as `Required User Actions`.

- The user logs onto keycoak. After authentication in the login form, keycloak asks them to register ther authenticator.


## Notes

### User Registration in Authentication with Resident Key supported Authenticator Scenario

Browser Flow (Use `Resident Key`) automatically asks users to authenticate on their authenticators. Therefore, the users without their accounts have no chance to register them on this flow.

For such the users to register their accounts, please use the default Browser Flow. It is helpful to user `Authentication Flow Overrides` on Client Settings. You can set the default Browser Flow for User Account Service (Client ID: account) to let users register their accounts at first.

### Requiring Resident Key in Registration

On registration, the browser asks you if you would like to store ID and its credential on your authenticator(namely, Resident Key). If you push OK button, the browser tells your authenticator to do so explicitly. If not, whether ID and its credential is Resident Key or not depends on authenticators.

Please note the followings:


- In Authentication with Resident Key supported Authenticator Scenario, only user's ID and its credential as Resident Key can be valid. Therefore, if you register ID and its credential that is not as Resident Key and try to authenticate with them, you fail to authenticate.


- Not all authenticators are capable of this Resident Key. The Authenticator lack of Resident Key capability fails to register user's ID and its credential when Resident Key is required explicitly.


- Not all browsers support this Resident Key. At least, I've confirmed that Microsoft Edge (ver.44) supports Resident Key.


