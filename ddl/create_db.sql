

load classes ../jars/volt-securetokens.jar;

file -inlinebatch END_OF_BATCH

create table token_users
(userid bigint not null primary key
,token_count bigint default 0  not null);

PARTITION TABLE token_users ON COLUMN userid;


create table secure_token_table
(userid bigint not null
,token_id varchar(80)  not null
,remaining_usages bigint not null
,expiry_date timestamp not null
,create_date timestamp not null
,primary key (userid, token_id))
USING TTL 180 MINUTES ON COLUMN expiry_date BATCH_SIZE 200 MAX_FREQUENCY 1;

CREATE INDEX ust_del_idx1 ON secure_token_table(expiry_date);

PARTITION TABLE secure_token_table ON COLUMN userid;

CREATE INDEX uut_ix1 ON secure_token_table(userid, expiry_date);

create table user_recent_transactions
 --MIGRATE TO TARGET user_old_transactions
(userid bigint not null 
,user_txn_id varchar(128) NOT NULL
,txn_time TIMESTAMP DEFAULT NOW  not null 
,purpose  varchar(128)
,primary key (userid, user_txn_id))
USING TTL 3600 SECONDS ON COLUMN txn_time BATCH_SIZE 200 MAX_FREQUENCY 1;

PARTITION TABLE user_recent_transactions ON COLUMN userid;

CREATE INDEX urt_del_idx ON user_recent_transactions(userid, txn_time,user_txn_id) ;

--CREATE INDEX urt_del_idx2 ON user_recent_transactions(userid, txn_time)  WHERE NOT MIGRATING;

CREATE INDEX urt_del_idx3 ON user_recent_transactions(txn_time);



CREATE STREAM user_error_events 
EXPORT TO TOPIC user_error_events 
WITH KEY (userid)
partition on column userid
(userid bigint not null 
,user_txn_id varchar(128) not null
,status_code tinyint not null
,message varchar(80) not null);

CREATE VIEW user_error_events_summary AS 
SELECT userid, count(*) how_many
FROM user_error_events
GROUP BY userid;


CREATE PROCEDURE 
   PARTITION ON TABLE secure_token_table COLUMN userid
   FROM CLASS tokens.CreateToken;  

CREATE PROCEDURE 
   PARTITION ON TABLE secure_token_table COLUMN userid
   FROM CLASS tokens.UseToken;  


END_OF_BATCH
