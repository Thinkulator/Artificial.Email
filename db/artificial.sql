\c artificial

-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: accounts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE accounts (
    account_id bigint NOT NULL,
    username text,
    password text,
    allowed_addr inet[]
);


ALTER TABLE public.accounts OWNER TO postgres;

--
-- Name: get_account_full(bigint, bigint); Type: FUNCTION; Schema: public; Owner: artificial_admin
--

CREATE FUNCTION get_account_full(bigint, bigint) RETURNS SETOF accounts
    LANGUAGE sql SECURITY DEFINER COST 2 ROWS 1
    AS $_$
select accounts.* from account_users inner join accounts on account_users.account_id = accounts.account_id where user_id = $1 and accounts.account_id = $2
$_$;


ALTER FUNCTION public.get_account_full(bigint, bigint) OWNER TO artificial_admin;

--
-- Name: get_accountid(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION get_accountid(text) RETURNS bigint
    LANGUAGE plpgsql SECURITY DEFINER
    AS $_$ 
DECLARE retval bigint; 
begin 
select account_id into retval from accounts where username=$1; 
return retval; 
end $_$;


ALTER FUNCTION public.get_accountid(text) OWNER TO postgres;

--
-- Name: sasl_lookup(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION sasl_lookup(text) RETURNS text
    LANGUAGE sql SECURITY DEFINER
    AS $_$ SELECT password from accounts where username=$1 $_$;


ALTER FUNCTION public.sasl_lookup(text) OWNER TO postgres;

--
-- Name: account_headers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE account_headers (
    account_id bigint,
    name text,
    status integer,
    ordinal integer
);


ALTER TABLE public.account_headers OWNER TO postgres;

--
-- Name: account_users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE account_users (
    account_id bigint NOT NULL,
    user_id bigint NOT NULL,
    status integer
);


ALTER TABLE public.account_users OWNER TO postgres;

--
-- Name: accounts_account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE accounts_account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.accounts_account_id_seq OWNER TO postgres;

--
-- Name: accounts_account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE accounts_account_id_seq OWNED BY accounts.account_id;


--
-- Name: accounts_relay; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW accounts_relay AS
    SELECT accounts.account_id, accounts.allowed_addr FROM accounts;


ALTER TABLE public.accounts_relay OWNER TO postgres;

--
-- Name: accounts_spool; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW accounts_spool AS
    SELECT accounts.account_id, accounts.username, accounts.allowed_addr FROM accounts;


ALTER TABLE public.accounts_spool OWNER TO postgres;

--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE messages (
    id bigint NOT NULL,
    account_id bigint,
    smtp_message_id text,
    from_addr text,
    subject text,
    message text,
    delivered_by text,
    received timestamp with time zone,
    delivered timestamp with time zone,
    remote_ip inet,
    recipients text[],
    headers text[],
    length integer
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO postgres;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE messages_id_seq OWNED BY messages.id;


--
-- Name: account_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accounts ALTER COLUMN account_id SET DEFAULT nextval('accounts_account_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY messages ALTER COLUMN id SET DEFAULT nextval('messages_id_seq'::regclass);


--
-- Name: account_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY account_users
    ADD CONSTRAINT account_users_pkey PRIMARY KEY (account_id, user_id);


--
-- Name: accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (account_id);


--
-- Name: messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: accounts_username_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX accounts_username_idx ON accounts USING btree (username);


--
-- Name: ix_message_account; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX ix_message_account ON messages USING btree (account_id, received);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: accounts; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE accounts FROM PUBLIC;
REVOKE ALL ON TABLE accounts FROM postgres;
GRANT ALL ON TABLE accounts TO postgres;
GRANT INSERT ON TABLE accounts TO artificial_write;
GRANT SELECT ON TABLE accounts TO artificial_admin;


--
-- Name: accounts.account_id; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL(account_id) ON TABLE accounts FROM PUBLIC;
REVOKE ALL(account_id) ON TABLE accounts FROM postgres;
GRANT SELECT(account_id) ON TABLE accounts TO artificial_write;


--
-- Name: get_account_full(bigint, bigint); Type: ACL; Schema: public; Owner: artificial_admin
--

REVOKE ALL ON FUNCTION get_account_full(bigint, bigint) FROM PUBLIC;
REVOKE ALL ON FUNCTION get_account_full(bigint, bigint) FROM artificial_admin;
GRANT ALL ON FUNCTION get_account_full(bigint, bigint) TO artificial_admin;
GRANT ALL ON FUNCTION get_account_full(bigint, bigint) TO artificial_read;


--
-- Name: sasl_lookup(text); Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON FUNCTION sasl_lookup(text) FROM PUBLIC;
REVOKE ALL ON FUNCTION sasl_lookup(text) FROM postgres;
GRANT ALL ON FUNCTION sasl_lookup(text) TO postgres;
GRANT ALL ON FUNCTION sasl_lookup(text) TO PUBLIC;
GRANT ALL ON FUNCTION sasl_lookup(text) TO sasl;


--
-- Name: account_headers; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE account_headers FROM PUBLIC;
REVOKE ALL ON TABLE account_headers FROM postgres;
GRANT ALL ON TABLE account_headers TO postgres;
GRANT SELECT ON TABLE account_headers TO artificial_spool;
GRANT SELECT ON TABLE account_headers TO artificial_read;


--
-- Name: account_users; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE account_users FROM PUBLIC;
REVOKE ALL ON TABLE account_users FROM postgres;
GRANT ALL ON TABLE account_users TO postgres;
GRANT SELECT ON TABLE account_users TO artificial_read;
GRANT INSERT ON TABLE account_users TO artificial_write;
GRANT SELECT ON TABLE account_users TO artificial_admin;


--
-- Name: accounts_account_id_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE accounts_account_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE accounts_account_id_seq FROM postgres;
GRANT ALL ON SEQUENCE accounts_account_id_seq TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE accounts_account_id_seq TO artificial_write;


--
-- Name: accounts_relay; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE accounts_relay FROM PUBLIC;
REVOKE ALL ON TABLE accounts_relay FROM postgres;
GRANT ALL ON TABLE accounts_relay TO postgres;
GRANT SELECT ON TABLE accounts_relay TO postfix;


--
-- Name: accounts_spool; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE accounts_spool FROM PUBLIC;
REVOKE ALL ON TABLE accounts_spool FROM postgres;
GRANT ALL ON TABLE accounts_spool TO postgres;
GRANT SELECT ON TABLE accounts_spool TO artificial_spool;
GRANT SELECT ON TABLE accounts_spool TO artificial_read;


--
-- Name: messages; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE messages FROM PUBLIC;
REVOKE ALL ON TABLE messages FROM postgres;
GRANT ALL ON TABLE messages TO postgres;
GRANT INSERT ON TABLE messages TO artificial_spool;
GRANT SELECT ON TABLE messages TO artificial_read;


--
-- Name: messages_id_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE messages_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE messages_id_seq FROM postgres;
GRANT ALL ON SEQUENCE messages_id_seq TO postgres;
GRANT USAGE ON SEQUENCE messages_id_seq TO artificial_spool;


--
-- PostgreSQL database dump complete
--

