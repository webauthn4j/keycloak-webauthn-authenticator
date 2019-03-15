# Keycloak WebAuthn Authenticator

[![Build Status](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator.svg?branch=master)](https://travis-ci.org/webauthn4j/keycloak-webauthn-authenticator)

[Web Authentication](https://www.w3.org/TR/webauthn/)(WebAuthn) sample plugin for [Keycloak](https://www.keycloak.org) , implements with [webauthn4j](https://github.com/webauthn4j/webauthn4j).

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
| WebAutn Register                       |                            | REQUIRED    |

### Browser Flow (2 Factor Authentication)

| Auth Type                    |                        | Requirement |
| ---------------------------- | ---------------------- | ----------- |
| Cookie                       |                        | ALTERNATIVE |
| Kerberos                     |                        | DISABLED    |
| Identity Provider Redirector |                        | ALTERNATIVE |
| Copy of Browser Flow         |                        | ALTERNATIVE |
|                              | Username Password Form | REQUIRED    |
|                              | OTP Form               | OPTIONAL    |
|                              | WebAutn Authenticator  | REQUIRED    |

### Browser Flow (Use `Resident Key`)

| Auth Type                    |     | Requirement |
| ---------------------------- | --- | ----------- |
| Cookie                       |     | ALTERNATIVE |
| Kerberos                     |     | DISABLED    |
| Identity Provider Redirector |     | ALTERNATIVE |
| WebAutn Authenticator        |     | REQUIRED    |

## TODO

- [x] Unit Test
- [ ] CI Integration

_TBD_
