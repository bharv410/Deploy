package com.kidgeniusdesigns.deployapp;

public class Contact
{
    String name;
    String num;
    boolean selected;

    public Contact(String name, String numb)
    {
        this.name = name;
        this.num = numb;
        selected = false;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNum()
    {
        return num;
    }

    public void setNum(String numb)
    {
        this.num = numb;
    }

    public boolean isChecked()
    {
        return this.selected;
    }

    public void setChecked(boolean selected)
    {
        this.selected = selected;
    }
}
