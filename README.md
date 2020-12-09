# accountBalanceManager

# 1 step
Run application (AccountBalanceManagerApplication file)
H2 - in memory Database is used, no need additional setup, just run the application

# 2 step

Import yourBankStatements.csv file 

Enter request URL: http://localhost:8080/api/import (POST)
In Postman, "Body" section, choose form-data, set KEY: file, type: file, value: select YourBankStatements.csv file

# 3 step

Export csv for given dates period, enter URL in your browser: http://localhost:8080/api/export?dateFrom=2020-12-01&dateTo=2020-12-10
to download bankStatements.csv file for given dates period. 
*dateFrom and *dateTo are optionals.

#4 step
To calculate balance use: http://localhost:8080/api/balance?accNumber=GB94BARC10201530093459&dateFrom=2020-12-07&dateTo=2020-12-08 (GET)
*accNumber is mandatory
*dateFrom and *dateTo are optionals.

# My bankStatements.csv file

accountNumber,operationDate,beneficiary,comment,amount,currency

GB33BUKB20201555555555,2020-12-06,Roman Atwood,Gift,100,EUR

GB94BARC10201530093459,2020-12-01,John Doe,,110,EUR

GB33BUKB20201555555555,2020-12-07,Roman Atwood,Drinks,50,EUR

GB33BUKB20201555555555,2020-12-09,Roman Atwood,,100,EUR

GB33BUKB20201555555555,2020-12-12,Roman Atwood,Car Headlights,250,EUR

GB94BARC10201530093459,2020-12-04,John Doe,,110,EUR

GB94BARC10201530093459,2020-12-07,John Doe,Car tail lights,150.56,EUR

GB94BARC10201530093459,2020-12-08,John Doe,Insurance,85.65,EUR


