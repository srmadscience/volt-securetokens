# volt-securetokens
Simple demo to show how Volt can be used to provide secure, unguessable tokens, each with a finite number of uses and an expiry date

```
DavidsMacBookPro5:EclipseWorkspace drolfe$ cd volt-securetokens/
DavidsMacBookPro5:volt-securetokens drolfe$ cd jars
DavidsMacBookPro5:jars drolfe$ ls
volt-securetokens-client.jar	volt-securetokens.jar
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost
2022-09-09 12:22:26:Parameters:[localhost]
2022-09-09 12:22:26:Usage: hostnames CREATE_USERS how_many 
2022-09-09 12:22:26:or 
2022-09-09 12:22:26:Usage: hostnames GET_TOKEN userid 
2022-09-09 12:22:26:or 
2022-09-09 12:22:26:Usage: hostnames USE_TOKEN userid token 
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost create_user 10000
2022-09-09 12:22:35:Parameters:[localhost, create_user, 10000]
2022-09-09 12:22:35:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:22:35:Connect to localhost...
2022-09-09 12:22:35:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost create_users 10000
2022-09-09 12:22:40:Parameters:[localhost, create_users, 10000]
2022-09-09 12:22:40:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:22:40:Connect to localhost...
2022-09-09 12:22:40:Upserted 1 users...
2022-09-09 12:22:40:All 10000 entries in queue, waiting for it to drain...
2022-09-09 12:22:40:Upserted 35 users per ms...
2022-09-09 12:22:40:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost get_token 42;
2022-09-09 12:22:53:Parameters:[localhost, get_token, 42]
2022-09-09 12:22:53:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:22:53:Connect to localhost...
2022-09-09 12:22:53:Created token
2022-09-09 12:22:53:modified_tuples 
----------------
               1

2022-09-09 12:22:53:modified_tuples 
----------------
               1

2022-09-09 12:22:53:modified_tuples 
----------------
               1

2022-09-09 12:22:53:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  5 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:22:53:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:12:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:12:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:13:Connect to localhost...
2022-09-09 12:23:13:subtracted one from token
2022-09-09 12:23:13:modified_tuples 
----------------
               1

2022-09-09 12:23:13:modified_tuples 
----------------
               1

2022-09-09 12:23:13:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  4 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:23:13:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:16:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:16:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:16:Connect to localhost...
2022-09-09 12:23:16:subtracted one from token
2022-09-09 12:23:16:modified_tuples 
----------------
               1

2022-09-09 12:23:16:modified_tuples 
----------------
               1

2022-09-09 12:23:16:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  3 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:23:16:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:17:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:17:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:17:Connect to localhost...
2022-09-09 12:23:18:subtracted one from token
2022-09-09 12:23:18:modified_tuples 
----------------
               1

2022-09-09 12:23:18:modified_tuples 
----------------
               1

2022-09-09 12:23:18:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  2 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:23:18:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:18:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:18:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:19:Connect to localhost...
2022-09-09 12:23:19:subtracted one from token
2022-09-09 12:23:19:modified_tuples 
----------------
               1

2022-09-09 12:23:19:modified_tuples 
----------------
               1

2022-09-09 12:23:19:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  1 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:23:19:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:19:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:19:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:19:Connect to localhost...
2022-09-09 12:23:19:subtracted one from token
2022-09-09 12:23:19:modified_tuples 
----------------
               1

2022-09-09 12:23:19:modified_tuples 
----------------
               1

2022-09-09 12:23:19:USERID  TOKEN_ID                                    REMAINING_USAGES  EXPIRY_DATE                 CREATE_DATE                
------- ------------------------------------------- ----------------- --------------------------- ---------------------------
     42 4221928626031402934-7416234202155678883-24                  0 2022-09-09 12:32:53.915000  2022-09-09 12:22:53.918000 

2022-09-09 12:23:19:Closing connection...
DavidsMacBookPro5:jars drolfe$ java -jar volt-securetokens-client.jar localhost use_token 42 '4221928626031402934-7416234202155678883-24';
2022-09-09 12:23:22:Parameters:[localhost, use_token, 42, 4221928626031402934-7416234202155678883-24]
2022-09-09 12:23:22:Logging into VoltDB
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by io.netty_voltpatches.NinjaKeySet (file:/Users/drolfe/Desktop/EclipseWorkspace/volt-securetokens/jars/volt-securetokens-client.jar) to field sun.nio.ch.SelectorImpl.selectedKeys
WARNING: Please consider reporting this to the maintainers of io.netty_voltpatches.NinjaKeySet
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-09-09 12:23:22:Connect to localhost...
2022-09-09 12:23:22:Token 4221928626031402934-7416234202155678883-24 used
2022-09-09 12:23:22:modified_tuples 
----------------
               1

2022-09-09 12:23:22:modified_tuples 
----------------
               1

2022-09-09 12:23:22:Closing connection...
```