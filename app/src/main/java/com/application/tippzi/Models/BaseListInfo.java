package com.application.tippzi.Models;

/**
 * Created by E on 6/28/2017.
 */

public class BaseListInfo {
    protected int m_nDataType;

    public BaseListInfo()
    {
        m_nDataType = 0;
    }

    public void setDataType(int dataType)
    {
        m_nDataType = dataType;
    }

    public int getDataType()
    {
        return m_nDataType;
    }
}
