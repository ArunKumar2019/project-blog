# project-blog
Blogging application
SELECT DISTINCT
    iln.INSTALL_LOAN_NO, 
    iln.LOAN_SYS_CRT_DT, 
    iln.OUTLET_ID, 
    iln.CUST_ID_NO, 
    iln.ACCT_NO, 
    iln.MTN, 
    iln.MTN_EFF_DT, 
    ils.LN_ST_LOAN_STAT_EFF_TS, 
    'UDP' AS UDP,
    cc.EFF_DT, 
    cc.CUST_ID_NO AS CC_CUST_ID_NO, 
    cc.ACCT_NO AS CC_ACCT_NO, 
    cc.SLS_REP_ID, 
    cc.OUTLET_ID AS CC_OUTLET_ID, 
    cc.MTN_EFF_DT AS CC_MTN_EFF_DT, 
    cc.MTN AS CC_MTN, 
    cc.U2C
FROM (
    -- Query 1 (Loan and Status Data)
    SELECT 
        iln.INSTALL_LOAN_NO, 
        iln.LOAN_SYS_CRT_DT, 
        iln.OUTLET_ID, 
        iln.CUST_ID_NO, 
        iln.ACCT_NO, 
        iln.MTN, 
        iln.MTN_EFF_DT, 
        ils.LN_ST_LOAN_STAT_EFF_TS, 
        'UDP' AS UDP 
    FROM (
        SELECT 
            il.INSTALL_LOAN_NO, 
            il.LOAN_SYS_CRT_DT, 
            il.OUTLET_ID, 
            il.CUST_ID_NO, 
            il.ACCT_NO, 
            il.MTN, 
            il.MTN_EFF_DT 
        FROM INSTALLMENT_LOAN_LOAN_NO il
        WHERE il.LOAN_SYS_CRT_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
          AND il.LOAN_SYS_CRT_DT > (
              SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'DD/MM/YYYY'), 'YYYY-MM-DD') 
              FROM CMN_BATCH_PARMS 
              WHERE CMN_PARM_GRP_ID = 'EPCA' 
                AND CMN_PARM_ENT_ID = 'UDPDT' 
                AND CMN_PARM_SEQ_NO = +1
                AND ROWNUM = 1
          )
    ) iln
    LEFT JOIN (
        SELECT 
            INSTALL_LOAN_NO, 
            LN_ST_LOAN_STAT_EFF_TS 
        FROM INSTALLMENT_LOAN_STATUS 
        WHERE LN_ST_INSTL_LOAN_STAT_CD IN ('A', 'B', 'P')
          AND LOAN_STAT_END_TS = '9999-12-31-23.59.59.999999'
          AND LN_ST_LOAN_STAT_EFF_TS < TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD-HH24.MI.SS.FF6')
          AND LN_ST_LOAN_STAT_EFF_TS > (
              SELECT TO_CHAR(TO_TIMESTAMP(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD-HH24.MI.SS.FF6') 
              FROM CMN_BATCH_PARMS 
              WHERE CMN_PARM_GRP_ID = 'EPCA' 
                AND CMN_PARM_ENT_ID = 'UDPDT' 
                AND CMN_PARM_SEQ_NO = +1
                AND ROWNUM = 1
          )
    ) ils ON iln.INSTALL_LOAN_NO = ils.INSTALL_LOAN_NO
) iln
FULL OUTER JOIN (
    -- Query 2 (Contract Data)
    SELECT 
        EFF_DT, 
        CUST_ID_NO, 
        ACCT_NO, 
        SLS_REP_ID, 
        OUTLET_ID, 
        MTN_EFF_DT, 
        MTN, 
        'U2C' AS U2C 
    FROM CUST_CONTRACT 
    WHERE END_DT > TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
      AND CNTRCT_TERMS_ID IN (
          SELECT DISTINCT CNTRCT_TERMS_ID 
          FROM CNTRCT_TERMS 
          WHERE CTERMS_TERM >= 24 
            AND CNTRCT_TYP_CD = 'E'
      )
      AND EFF_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
      AND EFF_DT > (
          SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD') 
          FROM CMN_BATCH_PARMS 
          WHERE CMN_PARM_GRP_ID = 'EPCA' 
            AND CMN_PARM_ENT_ID = 'U2CDT' 
            AND CMN_PARM_SEQ_NO = +1
            AND ROWNUM = 1
      )
) cc 
ON iln.CUST_ID_NO = cc.CUST_ID_NO 
   AND iln.OUTLET_ID = cc.OUTLET_ID
ORDER BY 
    CUST_ID_NO ASC, 
    ACCT_NO ASC, 
    MTN ASC, 
    EFF_DT ASC
FETCH FIRST 100 ROWS ONLY;

============================================================================================================================


SELECT DISTINCT 
    CUST_ID_NO, 
    ACCT_NO, 
    OUTLET_ID, 
    INSTALL_LOAN_NO, 
    LOAN_SYS_CRT_DT, 
    MTN, 
    MTN_EFF_DT, 
    LN_ST_LOAN_STAT_EFF_TS, 
    'UDP' AS UDP,
    NULL AS EFF_DT, 
    NULL AS SLS_REP_ID, 
    NULL AS U2C
FROM INSTALLMENT_LOAN_LOAN_NO iln
LEFT JOIN INSTALLMENT_LOAN_STATUS ils 
    ON iln.INSTALL_LOAN_NO = ils.INSTALL_LOAN_NO
WHERE iln.LOAN_SYS_CRT_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND iln.LOAN_SYS_CRT_DT > (
      SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'DD/MM/YYYY'), 'YYYY-MM-DD')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA' 
        AND CMN_PARM_ENT_ID = 'UDPDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )
  AND ils.LN_ST_INSTL_LOAN_STAT_CD IN ('A', 'B', 'P')
  AND ils.LOAN_STAT_END_TS = '9999-12-31-23.59.59.999999'
  AND ils.LN_ST_LOAN_STAT_EFF_TS < TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD-HH24.MI.SS.FF6')
  AND ils.LN_ST_LOAN_STAT_EFF_TS > (
      SELECT TO_CHAR(TO_TIMESTAMP(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD-HH24.MI.SS.FF6')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA'
        AND CMN_PARM_ENT_ID = 'UDPDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )

UNION ALL

SELECT DISTINCT 
    CUST_ID_NO, 
    ACCT_NO, 
    OUTLET_ID, 
    NULL AS INSTALL_LOAN_NO, 
    NULL AS LOAN_SYS_CRT_DT, 
    MTN, 
    MTN_EFF_DT, 
    NULL AS LN_ST_LOAN_STAT_EFF_TS, 
    NULL AS UDP,
    EFF_DT, 
    SLS_REP_ID, 
    'U2C' AS U2C
FROM CUST_CONTRACT cc
WHERE cc.END_DT > TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND cc.CNTRCT_TERMS_ID IN (
      SELECT DISTINCT CNTRCT_TERMS_ID
      FROM CNTRCT_TERMS
      WHERE CTERMS_TERM >= 24 
        AND CNTRCT_TYP_CD = 'E'
    )
  AND cc.EFF_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND cc.EFF_DT > (
      SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA' 
        AND CMN_PARM_ENT_ID = 'U2CDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )

ORDER BY 
    CUST_ID_NO ASC, 
    ACCT_NO ASC, 
    MTN ASC, 
    EFF_DT ASC
FETCH FIRST 100 ROWS ONLY;
==============================================================================================================================================


SELECT DISTINCT 
    iln.CUST_ID_NO AS CUST_ID_NO, 
    iln.ACCT_NO AS ACCT_NO, 
    iln.OUTLET_ID AS OUTLET_ID, 
    iln.INSTALL_LOAN_NO AS INSTALL_LOAN_NO, 
    iln.LOAN_SYS_CRT_DT AS LOAN_SYS_CRT_DT, 
    iln.MTN AS MTN, 
    iln.MTN_EFF_DT AS MTN_EFF_DT, 
    ils.LN_ST_LOAN_STAT_EFF_TS AS LN_ST_LOAN_STAT_EFF_TS, 
    'UDP' AS STATUS_TYPE,
    NULL AS EFF_DT, 
    NULL AS SLS_REP_ID, 
    NULL AS U2C_STATUS
FROM INSTALLMENT_LOAN_LOAN_NO iln
LEFT JOIN INSTALLMENT_LOAN_STATUS ils 
    ON iln.INSTALL_LOAN_NO = ils.INSTALL_LOAN_NO
WHERE iln.LOAN_SYS_CRT_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND iln.LOAN_SYS_CRT_DT > (
      SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'DD/MM/YYYY'), 'YYYY-MM-DD')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA' 
        AND CMN_PARM_ENT_ID = 'UDPDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )
  AND ils.LN_ST_INSTL_LOAN_STAT_CD IN ('A', 'B', 'P')
  AND ils.LOAN_STAT_END_TS = '9999-12-31-23.59.59.999999'
  AND ils.LN_ST_LOAN_STAT_EFF_TS < TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD-HH24.MI.SS.FF6')
  AND ils.LN_ST_LOAN_STAT_EFF_TS > (
      SELECT TO_CHAR(TO_TIMESTAMP(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD-HH24.MI.SS.FF6')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA'
        AND CMN_PARM_ENT_ID = 'UDPDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )

UNION ALL

SELECT DISTINCT 
    cc.CUST_ID_NO AS CUST_ID_NO, 
    cc.ACCT_NO AS ACCT_NO, 
    cc.OUTLET_ID AS OUTLET_ID, 
    NULL AS INSTALL_LOAN_NO, 
    NULL AS LOAN_SYS_CRT_DT, 
    cc.MTN AS MTN, 
    cc.MTN_EFF_DT AS MTN_EFF_DT, 
    NULL AS LN_ST_LOAN_STAT_EFF_TS, 
    NULL AS STATUS_TYPE,
    cc.EFF_DT AS EFF_DT, 
    cc.SLS_REP_ID AS SLS_REP_ID, 
    'U2C' AS U2C_STATUS
FROM CUST_CONTRACT cc
WHERE cc.END_DT > TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND cc.CNTRCT_TERMS_ID IN (
      SELECT DISTINCT CNTRCT_TERMS_ID
      FROM CNTRCT_TERMS
      WHERE CTERMS_TERM >= 24 
        AND CNTRCT_TYP_CD = 'E'
    )
  AND cc.EFF_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
  AND cc.EFF_DT > (
      SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD')
      FROM CMN_BATCH_PARMS
      WHERE CMN_PARM_GRP_ID = 'EPCA' 
        AND CMN_PARM_ENT_ID = 'U2CDT'
        AND CMN_PARM_SEQ_NO = +1
        AND ROWNUM = 1
    )

ORDER BY 
    CUST_ID_NO ASC, 
    ACCT_NO ASC, 
    MTN ASC, 
    EFF_DT ASC
FETCH FIRST 100 ROWS ONLY;
===================================================================================================================================================

SELECT DISTINCT 
    COALESCE(iln.CUST_ID_NO, cc.CUST_ID_NO) AS CUST_ID_NO, 
    COALESCE(iln.ACCT_NO, cc.ACCT_NO) AS ACCT_NO, 
    COALESCE(iln.OUTLET_ID, cc.OUTLET_ID) AS OUTLET_ID,
    iln.INSTALL_LOAN_NO, 
    iln.LOAN_SYS_CRT_DT, 
    COALESCE(iln.MTN, cc.MTN) AS MTN, 
    COALESCE(iln.MTN_EFF_DT, cc.MTN_EFF_DT) AS MTN_EFF_DT, 
    ils.LN_ST_LOAN_STAT_EFF_TS, 
    'UDP' AS STATUS_TYPE, 
    cc.EFF_DT, 
    cc.SLS_REP_ID, 
    cc.U2C_STATUS
FROM (
    -- Primary query (for UDP data)
    SELECT 
        iln.install_loan_no, 
        iln.loan_sys_crt_dt, 
        iln.outlet_id, 
        iln.cust_id_no, 
        iln.acct_no, 
        iln.mtn, 
        iln.mtn_eff_dt, 
        ils.ln_st_loan_stat_eff_ts, 
        'UDP' AS STATUS_TYPE
    FROM INSTALLMENT_LOAN_LOAN_NO iln
    LEFT JOIN INSTALLMENT_LOAN_STATUS ils 
        ON iln.INSTALL_LOAN_NO = ils.INSTALL_LOAN_NO
    WHERE iln.LOAN_SYS_CRT_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
      AND iln.LOAN_SYS_CRT_DT > (
          SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'DD/MM/YYYY'), 'YYYY-MM-DD')
          FROM CMN_BATCH_PARMS
          WHERE CMN_PARM_GRP_ID = 'EPCA' 
            AND CMN_PARM_ENT_ID = 'UDPDT'
            AND CMN_PARM_SEQ_NO = +1
            AND ROWNUM = 1
      )
      AND ils.LN_ST_INSTL_LOAN_STAT_CD IN ('A', 'B', 'P')
      AND ils.LOAN_STAT_END_TS = '9999-12-31-23.59.59.999999'
      AND ils.LN_ST_LOAN_STAT_EFF_TS < TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD-HH24.MI.SS.FF6')
      AND ils.LN_ST_LOAN_STAT_EFF_TS > (
          SELECT TO_CHAR(TO_TIMESTAMP(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD-HH24.MI.SS.FF6')
          FROM CMN_BATCH_PARMS
          WHERE CMN_PARM_GRP_ID = 'EPCA'
            AND CMN_PARM_ENT_ID = 'UDPDT'
            AND CMN_PARM_SEQ_NO = +1
            AND ROWNUM = 1
      )
) iln
FULL OUTER JOIN (
    -- Secondary query (for U2C data)
    SELECT 
        cc.EFF_DT, 
        cc.CUST_ID_NO, 
        cc.ACCT_NO, 
        cc.SLS_REP_ID, 
        cc.OUTLET_ID, 
        cc.MTN_EFF_DT, 
        cc.MTN, 
        'U2C' AS U2C_STATUS
    FROM CUST_CONTRACT cc
    WHERE cc.END_DT > TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
      AND cc.CNTRCT_TERMS_ID IN (
          SELECT DISTINCT CNTRCT_TERMS_ID
          FROM CNTRCT_TERMS
          WHERE CTERMS_TERM >= 24 
            AND CNTRCT_TYP_CD = 'E'
      )
      AND cc.EFF_DT < TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')
      AND cc.EFF_DT > (
          SELECT TO_CHAR(TO_DATE(CMN_PARM_VALUE, 'MM/DD/YYYY'), 'YYYY-MM-DD')
          FROM CMN_BATCH_PARMS
          WHERE CMN_PARM_GRP_ID = 'EPCA' 
            AND CMN_PARM_ENT_ID = 'U2CDT'
            AND CMN_PARM_SEQ_NO = +1
            AND ROWNUM = 1
      )
) cc
ON iln.CUST_ID_NO = cc.CUST_ID_NO
   AND iln.ACCT_NO = cc.ACCT_NO
   AND iln.MTN = cc.MTN
ORDER BY 
    COALESCE(iln.CUST_ID_NO, cc.CUST_ID_NO) ASC, 
    COALESCE(iln.ACCT_NO, cc.ACCT_NO) ASC, 
    COALESCE(iln.MTN, cc.MTN) ASC, 
    COALESCE(iln.MTN_EFF_DT, cc.EFF_DT) ASC;

