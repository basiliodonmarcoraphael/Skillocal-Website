package com.example.skillocal_final;

import java.io.Serializable;
import java.util.List;

public class JobApplicationList implements Serializable {
    private List<JobApplication> list;

    public JobApplicationList(List<JobApplication> list) {
        this.list = list;
    }

    public List<JobApplication> getList() {
        return list;
    }
}