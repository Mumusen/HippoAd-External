package com.transmartx.hippo.model.excel;

import lombok.Data;

import java.util.List;


/**
 * @author: letxig
 */
@Data
public class SheetNoNameData {

    private int sheetNo;

    private String sheetName;

    private Class clazz;

    private List dataList;

    private SheetNoNameData() {}

    public SheetNoNameData(int sheetNo, String sheetName, Class clazz, List dataList) {
        this.sheetNo = sheetNo;
        this.sheetName = sheetName;
        this.clazz = clazz;
        this.dataList = dataList;
    }

    public static SheetNoNameData of(int sheetNo, String sheetName, Class clazz, List dataList) {
        return new SheetNoNameData(sheetNo, sheetName, clazz, dataList);
    }

}
