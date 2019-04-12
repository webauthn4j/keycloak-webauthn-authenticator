# Keycloak WebAuthn Authenticator

[![Build Status](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator.svg?branch=master)](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator)

[Web Authentication](https://www.w3.org/TR/webauthn/)(WebAuthn) sample plugin for [Keycloak](https://www.keycloak.org) , implements with [webauthn4j](https://github.com/webauthn4j/webauthn4j).

## Environment

We've confirmed that this demo had worked well under the following environments:

- 2 Factor Authentication with Resident Key Not supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Google Chrome (ver 73), Mozilla FireFox (ver 66)
  - Authenticator : Yubico Security Key
  - Server(RP) : keycloak-5.0.0 on localhost

- 2 Factor Authentication with Resident Key supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Microsoft Edge (ver 44)
  - Authenticator : Internal Fingerprint Authentication Device
  - Server(RP) : keycloak-5.0.0 on localhost

- Authentication with Resident Key supported Authenticator Scenario

  - OS : Windows 10
  - Browser : Microsoft Edge (ver 44)
  - Authenticator : Internal Fingerprint Authentication Device
  - Server(RP) : keycloak-5.0.0 on localhost

## Install

- build

  - `$ mvn install`

- Add the EAR file to the Keycloak Server
  - `$ cp webuahtn4j-ear/target/keycloak-webauthn4j-ear-*.ear $KEYCLOAK_HOME/standalone/deployment/`

## Authentication Flow Settings

### Realm Settings

- Enable `User registration` in 'Realm Settings' - 'Login'

### Registration Flow

| Auth Type                              |                            | Requirement |
| -------------------------------------- | -------------------------- | ----------- |
| Copy Of Registration Registration Form |                            | REQUIRED    |
|                                        | Registration User Creation | REQUIRED    |
|                                        | Profile Validation         | REQUIRED    |
|                                        | Password Validation        | REQUIRED    |
|                                        | Recaptcha                  | DISABLED    |
| WebAuthn Register                      |                            | REQUIRED    |

### Browser Flow (2 Factor Authentication)

| Auth Type                    |                        | Requirement |
| ---------------------------- | ---------------------- | ----------- |
| Cookie                       |                        | ALTERNATIVE |
| Kerberos                     |                        | DISABLED    |
| Identity Provider Redirector |                        | ALTERNATIVE |
| Copy of Browser Flow         |                        | ALTERNATIVE |
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

## Open Issue

We've not yet resolved the following issues:

- credential storage : avoid creating a new table for credentials, mentioned in [keycloak-dev ML](http://lists.jboss.org/pipermail/keycloak-dev/2019-March/011837.html).
- problem on re-build and re-deploy : after re-building and re-deploying this demo, there are some cases that the user registered before this re-building and re-deploying can not be authenticated.
- latest WebAuthn4j support : this demo has not yet supported the latest WebAuthn4j(0.9.2.RELEASE)

## TODO

- [x] Unit Test
- [ ] CI Integration

_TBD_
