[![Build Status](https://travis-ci.org/ggerashchenko/GistsAPI.svg?branch=master)](https://travis-ci.org/ggerashchenko/GistsAPI)

##  Document purpose
I will try briefly to explain how I wanted to approach testing, which difficulties I faced and what I actually was able to produce.

## Initial technical task:
The idea behind this test is to asses your knowledge of APIs and testing them.
The task is to create a series of automated tests that cover the CRUD
operations plus some edge cases based on Github Gists using the github API.
(Github gists are the tool todo that, knowledge on Gists should not be
required).
To do that we will use https://developer.github.com/v3/gists/ you will need an
account on git and probably create a oauth2 token.
https://blog.github.com/2013-05-16-personal-api-tokens/

## Original plan was:
1. Write tests in Java.
2. Use Restassured library for tests
3. Use JUnit as test runner.
4. Write next tests:
    * CRUD operations with Gists API
5. Use [Allure](http://allure.qatools.ru/) for reporting.
 
## What was achieved:
1. Maven project that has a series of API tests.
2. API tests include: 
    - CRUD tests with Gists API
3. After tests are finished, allure report is generated.

## Difficulties I have faced during testing : 
Initial test cases were written on pure rest assure library. Requests were returning JasonPath and I was working with stings for validating response.
But then I decided to rewrite string validations to data models. So I can work with objects instead of strings 

In order to run test you can use IDE or you need to call `mvn cleat test` task inside the project folder.
In order to get report you need to call `allure serve allure-results/` task inside the project folder. But to be able to do so
you need to install `allure` with `brew install allure`. In case you do not have `brew` you can look at the example
of the report in `allure-report-example/index.html`.

## What could be improved : 
- Data provider with various test data sets can be created. Data model can cover all request/response fields. Can be added logger to requests and responses.
