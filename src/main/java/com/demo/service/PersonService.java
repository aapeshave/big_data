package com.demo.service;


public interface PersonService {

    /**
     * Created Person using person data
     *
     * @param personData
     * @return
     */
    String processAndAddPerson(String personData);

    /**
     * Return person data
     * @param personUID
     * @return
     */
    String getPerson(String personUID);

    /**
     *
     * @param personBody
     * @return
     */
    String v1AddPerson(String personBody);

    /**
     *
     * @param persionUid
     * @return
     */
    String v1GetPerson(String persionUid);

    /**
     *
     * @param personId for the person
     * @param parameterName is the parameter that needs to be changed
     * @param parameterKey is the key of the parameter that needs to be changed
     * @param parameterValue is the value that needs to be replaced
     * @return true if update is successful, otherwise return false
     */
    Boolean newUpdatePerson(String personId, String parameterName, String parameterKey, String parameterValue);

}
