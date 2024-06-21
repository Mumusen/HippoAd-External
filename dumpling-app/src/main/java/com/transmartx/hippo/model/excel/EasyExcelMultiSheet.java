package com.transmartx.hippo.model.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author: letxig
 */
public class EasyExcelMultiSheet {

    private EasyExcelMultiSheet() {
    }

    public static ExcelWriter buildExcelWriter(FileOutputStream os) {
        return buildExcelWriter(os, null);
    }

    public static ExcelWriter buildExcelWriter(OutputStream os) {
        return buildExcelWriter(os, null);
    }

    public static ExcelWriter buildExcelWriter(OutputStream os, ExcelTypeEnum excelType) {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setOutputStream(os);
        if (excelType != null) {
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
        }
        return new ExcelWriter(writeWorkbook);
    }

    public static WriteSheet newWriteSheet(int sheetNo, String sheetName, Class clazz, AbstractCellStyleStrategy styleStrategy) {
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetName).build();
        if (styleStrategy != null) {
            writeSheet.getCustomWriteHandlerList().add(styleStrategy);
        }
        writeSheet.setClazz(clazz);
        return writeSheet;
    }

    public static void doWrite(ExcelWriter excelWriter, int sheetNo, String sheetName, Class clazz, AbstractCellStyleStrategy styleStrategy, List dataList) {
        excelWriter.write(dataList, newWriteSheet(sheetNo, sheetName, clazz, styleStrategy));
    }

    public static void doWrite(ExcelWriter excelWriter, WriteSheet sheet, List dataList) {
        excelWriter.write(dataList, sheet);
    }

    public static void finish(ExcelWriter excelWriter) {
        if (excelWriter != null) {
            excelWriter.finish();
        }
    }

}
