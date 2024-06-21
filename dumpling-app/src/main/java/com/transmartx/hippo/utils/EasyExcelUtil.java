package com.transmartx.hippo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.transmartx.hippo.model.excel.EasyExcelMultiSheet;
import com.transmartx.hippo.model.excel.SheetNoNameData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

public class EasyExcelUtil {

	public static <T> void write2File(String fileName, Class<T> clazz, List<T> list) throws Exception {
		write2File(fileName, clazz, list, null);
	}

	public static <T> void write2File(String fileName, List<List<String>> header, List<List<Object>> contentList) throws Exception {
		FileOutputStream os = new FileOutputStream(fileName);
		EasyExcel.write(os).head(header).excelType(ExcelTypeEnum.XLSX).sheet().registerWriteHandler(getCellStyleStrategy()).doWrite(contentList);
	}

	public static <T> void write2File(String fileName, Class<T> clazz, List<T> list, AbstractCellStyleStrategy cellStyleStrategy) throws Exception {
		FileOutputStream os = new FileOutputStream(fileName);
		write2OutputStream(os, clazz, list, cellStyleStrategy);
	}

	/**
	 * @param response
	 * @param name 文件名,不需要.xlsx后缀
	 * @param clazz
	 * @param list
	 * @param <T>
	 */
	public static <T> void wirte2Response(HttpServletResponse response, String name, Class<T> clazz, List<T> list) {
		try {
			OutputStream outPutStream = getExcelOutPutStream(response, name);
			write2OutputStream(outPutStream, clazz, list);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}


	/**
	 *
	 * @param response
	 * @param name 文件名,不需要.xlsx后缀
	 * @param sheetNoNameDataList
	 * @param <T>
	 */
	public static <T> void write2Response(HttpServletResponse response, String name, List<SheetNoNameData> sheetNoNameDataList) {
		try {
			OutputStream os = getExcelOutPutStream(response, name);
			write2FileWithSpecSheet(os, sheetNoNameDataList);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * @param response
	 * @param name 文件名,不需要.xlsx后缀
	 * @param clazz
	 * @param list
	 * @param <T>
	 */
	public static <T> void wirte2Response(HttpServletResponse response, String name, Class<T> clazz, List<T> list, AbstractCellStyleStrategy cellStyleStrategy) {
		try {
			OutputStream outPutStream = getExcelOutPutStream(response, name);
			write2OutputStream(outPutStream, clazz, list, cellStyleStrategy);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	public static <T> void write2OutputStream(OutputStream os, Class<T> clazz, List<T> list) {
		write2OutputStream(os, clazz, list, null);
	}

	public static <T> void write2OutputStream(OutputStream os, Class<T> clazz, List<T> list, AbstractCellStyleStrategy cellStyleStrategy) {
		// 没有设置样式,使用默认的
		if (cellStyleStrategy == null) {
			cellStyleStrategy = getCellStyleStrategy();
		}
		EasyExcel.write(os, clazz).excelType(ExcelTypeEnum.XLSX).sheet().registerWriteHandler(cellStyleStrategy).doWrite(list);
	}

	public static AbstractCellStyleStrategy getCellStyleStrategy() {
		AbstractCellStyleStrategy cellStyleStrategy;// 标题栏格式
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setFontHeightInPoints((short)10);
		headWriteCellStyle.setWriteFont(headWriteFont);

		// 正文单元格格式
		WriteCellStyle contentCellStyle = new WriteCellStyle();
		contentCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
		contentCellStyle.setBorderBottom(BorderStyle.THIN);
		contentCellStyle.setBorderLeft(BorderStyle.THIN);
		contentCellStyle.setBorderRight(BorderStyle.THIN);
		contentCellStyle.setBorderTop(BorderStyle.THIN);

		cellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentCellStyle);
		return cellStyleStrategy;
	}

	/**
	 * 获取输出流，并设置下载excel的header
	 *
	 * @param response
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static OutputStream getExcelOutPutStream(HttpServletResponse response, String file) throws Exception {
		// 这里注意
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
		String fileName = URLEncoder.encode(file, "UTF-8");
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		return response.getOutputStream();
	}


	public static <T> void write2FileWithSpecSheet(String fileName, List<SheetNoNameData> sheetNoNameDataList) throws Exception {
		if (sheetNoNameDataList == null) throw new IllegalArgumentException("sheetNoNameMap can not be null");
		write2FileWithSpecSheet(fileName, null, sheetNoNameDataList);
	}

	public static <T> void write2FileWithSpecSheet(String fileName, AbstractCellStyleStrategy cellStyleStrategy, List<SheetNoNameData> sheetNoNameDataList) throws Exception {
		if (CollectionUtils.isNotEmpty(sheetNoNameDataList)) {
			FileOutputStream os = new FileOutputStream(fileName);
			if (cellStyleStrategy == null) {
				cellStyleStrategy = getCellStyleStrategy();
			}
			ExcelWriter excelWriter = EasyExcelMultiSheet.buildExcelWriter(os);
			for (SheetNoNameData s : sheetNoNameDataList) {
				int sheetNo = s.getSheetNo();
				String sheetName = s.getSheetName();
				List dataList = s.getDataList();
				Class clazz = s.getClazz();
				EasyExcelMultiSheet.doWrite(excelWriter, sheetNo, sheetName, clazz, cellStyleStrategy, dataList);
			}
			EasyExcelMultiSheet.finish(excelWriter);
		}
	}

	public static <T> void write2FileWithSpecSheet(OutputStream os, List<SheetNoNameData> sheetNoNameDataList) throws Exception {
		if (sheetNoNameDataList == null) throw new IllegalArgumentException("sheetNoNameMap can not be null");
		write2FileWithSpecSheet(os, null, sheetNoNameDataList);
	}

	public static <T> void write2FileWithSpecSheet(OutputStream os, AbstractCellStyleStrategy cellStyleStrategy, List<SheetNoNameData> sheetNoNameDataList) throws Exception {
		if (CollectionUtils.isNotEmpty(sheetNoNameDataList)) {
			if (cellStyleStrategy == null) {
				cellStyleStrategy = getCellStyleStrategy();
			}
			ExcelWriter excelWriter = EasyExcelMultiSheet.buildExcelWriter(os);
			for (SheetNoNameData s : sheetNoNameDataList) {
				int sheetNo = s.getSheetNo();
				String sheetName = s.getSheetName();
				List dataList = s.getDataList();
				Class clazz = s.getClazz();
				EasyExcelMultiSheet.doWrite(excelWriter, sheetNo, sheetName, clazz, cellStyleStrategy, dataList);
			}
			EasyExcelMultiSheet.finish(excelWriter);
		}
	}

	public static <T> void write2File(String fileName, Class<T> clazz, List<T> list, AbstractCellStyleStrategy cellStyleStrategy, int sheetNo, String sheetName) throws Exception {
		FileOutputStream os = new FileOutputStream(fileName);
		write2OutputStream(os, clazz, list, cellStyleStrategy, sheetNo, sheetName);
	}

	public static <T> void write2OutputStream(OutputStream os, Class<T> clazz, List<T> list, AbstractCellStyleStrategy cellStyleStrategy, int sheetNo, String sheetName) {
		// 没有设置样式,使用默认的
		if (cellStyleStrategy == null) {
			cellStyleStrategy = getCellStyleStrategy();
		}
		EasyExcel.write(os, clazz).excelType(ExcelTypeEnum.XLSX).sheet(sheetNo, sheetName).registerWriteHandler(cellStyleStrategy).doWrite(list);
	}
}
