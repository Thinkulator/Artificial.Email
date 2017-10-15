\c thinkulator
--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
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
-- Name: persistent_cookies; Type: TABLE; Schema: public; Owner: thinkulator; Tablespace: 
--

CREATE TABLE persistent_cookies (
    cookie bytea NOT NULL,
    status integer DEFAULT 0,
    last_used_email text,
    auto_auth_status integer DEFAULT 0
);


ALTER TABLE public.persistent_cookies OWNER TO thinkulator;

--
-- Name: cookie_auth(bytea); Type: FUNCTION; Schema: public; Owner: thinkulator
--

CREATE FUNCTION cookie_auth(bytea) RETURNS SETOF persistent_cookies
    LANGUAGE sql SECURITY DEFINER ROWS 1
    AS $_$
    SELECT * FROM persistent_cookies WHERE cookie = $1 and status != 0;
$_$;


ALTER FUNCTION public.cookie_auth(bytea) OWNER TO thinkulator;

--
-- Name: users_shadow; Type: TABLE; Schema: public; Owner: thinkulator; Tablespace: 
--

CREATE TABLE users_shadow (
    user_id bigint NOT NULL,
    email text NOT NULL,
    enc text NOT NULL,
    pass text NOT NULL,
    display text NOT NULL,
    status integer DEFAULT 0,
    created_at timestamp with time zone DEFAULT now(),
    last_updated timestamp with time zone DEFAULT now(),
    registration_key text
);


ALTER TABLE public.users_shadow OWNER TO thinkulator;

--
-- Name: users_auth(text, text); Type: FUNCTION; Schema: public; Owner: thinkulator
--

CREATE FUNCTION users_auth(text, text) RETURNS SETOF users_shadow
    LANGUAGE sql SECURITY DEFINER COST 1 ROWS 1
    AS $_$
    SELECT * FROM users_shadow WHERE lower(email)=lower($1) and lower(pass)=lower($2);
$_$;


ALTER FUNCTION public.users_auth(text, text) OWNER TO thinkulator;

--
-- Name: users_get(text); Type: FUNCTION; Schema: public; Owner: thinkulator
--

CREATE FUNCTION users_get(text) RETURNS SETOF users_shadow
    LANGUAGE sql SECURITY DEFINER COST 1 ROWS 1
    AS $_$
    SELECT  user_id, email, enc, '******'::text as pass, display, status,created_at,last_updated,'******'::text as registration_key from users_shadow WHERE lower(email)=lower($1)
$_$;


ALTER FUNCTION public.users_get(text) OWNER TO thinkulator;

--
-- Name: users_get_id(bigint); Type: FUNCTION; Schema: public; Owner: thinkulator_write
--

CREATE FUNCTION users_get_id(bigint) RETURNS SETOF users_shadow
    LANGUAGE sql SECURITY DEFINER
    AS $_$                                                                                                                                                     
     SELECT  user_id, email, enc, '******'::text as pass, display, status,created_at,last_updated,'******'::text as registration_key from users_shadow WHERE user_id=$1
 $_$;


ALTER FUNCTION public.users_get_id(bigint) OWNER TO thinkulator_write;

--
-- Name: users_reg_clear(text); Type: FUNCTION; Schema: public; Owner: thinkulator_write
--

CREATE FUNCTION users_reg_clear(text) RETURNS void
    LANGUAGE sql
    AS $_$
    update users_shadow set registration_key=null WHERE lower(email)=lower($1);
$_$;


ALTER FUNCTION public.users_reg_clear(text) OWNER TO thinkulator_write;

--
-- Name: users_register(text, text); Type: FUNCTION; Schema: public; Owner: thinkulator_write
--

CREATE FUNCTION users_register(text, text) RETURNS SETOF users_shadow
    LANGUAGE sql ROWS 1
    AS $_$
    SELECT * FROM users_shadow WHERE lower(email)=lower($1) and lower(registration_key)=lower($2);
$_$;


ALTER FUNCTION public.users_register(text, text) OWNER TO thinkulator_write;

--
-- Name: users_shadow_user_id_seq; Type: SEQUENCE; Schema: public; Owner: thinkulator
--

CREATE SEQUENCE users_shadow_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_shadow_user_id_seq OWNER TO thinkulator;

--
-- Name: users_shadow_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: thinkulator
--

ALTER SEQUENCE users_shadow_user_id_seq OWNED BY users_shadow.user_id;


--
-- Name: user_id; Type: DEFAULT; Schema: public; Owner: thinkulator
--

ALTER TABLE ONLY users_shadow ALTER COLUMN user_id SET DEFAULT nextval('users_shadow_user_id_seq'::regclass);


--
-- Name: persistent_cookies_pkey; Type: CONSTRAINT; Schema: public; Owner: thinkulator; Tablespace: 
--

ALTER TABLE ONLY persistent_cookies
    ADD CONSTRAINT persistent_cookies_pkey PRIMARY KEY (cookie);


--
-- Name: users_shadow_pkey; Type: CONSTRAINT; Schema: public; Owner: thinkulator; Tablespace: 
--

ALTER TABLE ONLY users_shadow
    ADD CONSTRAINT users_shadow_pkey PRIMARY KEY (user_id);


--
-- Name: ix_persistent_cookies_unused; Type: INDEX; Schema: public; Owner: thinkulator; Tablespace: 
--

CREATE INDEX ix_persistent_cookies_unused ON persistent_cookies USING btree (cookie) WHERE (status = 0);


--
-- Name: uix_users_email; Type: INDEX; Schema: public; Owner: thinkulator; Tablespace: 
--

CREATE UNIQUE INDEX uix_users_email ON users_shadow USING btree (lower(email));


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: persistent_cookies; Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON TABLE persistent_cookies FROM PUBLIC;
REVOKE ALL ON TABLE persistent_cookies FROM thinkulator;
GRANT ALL ON TABLE persistent_cookies TO thinkulator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE persistent_cookies TO thinkulator_write;


--
-- Name: cookie_auth(bytea); Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON FUNCTION cookie_auth(bytea) FROM PUBLIC;
REVOKE ALL ON FUNCTION cookie_auth(bytea) FROM thinkulator;
GRANT ALL ON FUNCTION cookie_auth(bytea) TO thinkulator;
GRANT ALL ON FUNCTION cookie_auth(bytea) TO PUBLIC;
GRANT ALL ON FUNCTION cookie_auth(bytea) TO thinkulator_read;


--
-- Name: users_shadow; Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON TABLE users_shadow FROM PUBLIC;
REVOKE ALL ON TABLE users_shadow FROM thinkulator;
GRANT ALL ON TABLE users_shadow TO thinkulator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE users_shadow TO thinkulator_write;


--
-- Name: users_auth(text, text); Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON FUNCTION users_auth(text, text) FROM PUBLIC;
REVOKE ALL ON FUNCTION users_auth(text, text) FROM thinkulator;
GRANT ALL ON FUNCTION users_auth(text, text) TO thinkulator;
GRANT ALL ON FUNCTION users_auth(text, text) TO thinkulator_read;


--
-- Name: users_get(text); Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON FUNCTION users_get(text) FROM PUBLIC;
REVOKE ALL ON FUNCTION users_get(text) FROM thinkulator;
GRANT ALL ON FUNCTION users_get(text) TO thinkulator;
GRANT ALL ON FUNCTION users_get(text) TO thinkulator_read;
GRANT ALL ON FUNCTION users_get(text) TO thinkulator_write;


--
-- Name: users_get_id(bigint); Type: ACL; Schema: public; Owner: thinkulator_write
--

REVOKE ALL ON FUNCTION users_get_id(bigint) FROM PUBLIC;
REVOKE ALL ON FUNCTION users_get_id(bigint) FROM thinkulator_write;
GRANT ALL ON FUNCTION users_get_id(bigint) TO thinkulator_write;
GRANT ALL ON FUNCTION users_get_id(bigint) TO thinkulator;
GRANT ALL ON FUNCTION users_get_id(bigint) TO thinkulator_read;


--
-- Name: users_reg_clear(text); Type: ACL; Schema: public; Owner: thinkulator_write
--

REVOKE ALL ON FUNCTION users_reg_clear(text) FROM PUBLIC;
REVOKE ALL ON FUNCTION users_reg_clear(text) FROM thinkulator_write;
GRANT ALL ON FUNCTION users_reg_clear(text) TO thinkulator_write;
GRANT ALL ON FUNCTION users_reg_clear(text) TO thinkulator;
GRANT ALL ON FUNCTION users_reg_clear(text) TO thinkulator_read;


--
-- Name: users_register(text, text); Type: ACL; Schema: public; Owner: thinkulator_write
--

REVOKE ALL ON FUNCTION users_register(text, text) FROM PUBLIC;
REVOKE ALL ON FUNCTION users_register(text, text) FROM thinkulator_write;
GRANT ALL ON FUNCTION users_register(text, text) TO thinkulator_write;
GRANT ALL ON FUNCTION users_register(text, text) TO thinkulator;
GRANT ALL ON FUNCTION users_register(text, text) TO thinkulator_read;


--
-- Name: users_shadow_user_id_seq; Type: ACL; Schema: public; Owner: thinkulator
--

REVOKE ALL ON SEQUENCE users_shadow_user_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE users_shadow_user_id_seq FROM thinkulator;
GRANT ALL ON SEQUENCE users_shadow_user_id_seq TO thinkulator;
GRANT SELECT,UPDATE ON SEQUENCE users_shadow_user_id_seq TO thinkulator_write;


--
-- PostgreSQL database dump complete
--

