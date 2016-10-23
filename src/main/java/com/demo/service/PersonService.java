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
}
