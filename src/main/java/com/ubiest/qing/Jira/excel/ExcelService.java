package com.ubiest.qing.Jira.excel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.worklog.WorklogHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelService {
	
	private final static short COLUMN_SIZE = 7;
	
	public HttpServletResponse configureExcel(LocalDate startDate, String username, HttpServletResponse response) {
		String fileName = username + "-" + startDate.getMonth().toString() + ".xlsx";
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		return response;
	}
	
	public Workbook createWorklogExcelFile(WorklogHandler worklogResource,
			String username, LocalDate from, LocalDate to) throws IOException {
		
		Workbook workbook = new XSSFWorkbook();
		
		log.info("Creating timesheet for user {} at month {}", username, from.getMonth().toString());
		
		Sheet sheet;
		sheet = workbook.createSheet("Timesheet");
		
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		CellStyle alignCellStyle = workbook.createCellStyle();
		alignCellStyle.setAlignment(HorizontalAlignment.CENTER);
		alignCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setAlignment(HorizontalAlignment.CENTER);
		numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
		
		int rowNum = 1;
		
		rowNum = createSheetTitle(username, from, sheet, headerCellStyle, alignCellStyle, rowNum);
		
		rowNum = createSheetBody(worklogResource, from, to, sheet, alignCellStyle, numberStyle, rowNum);
		
		createSheetFooter(sheet, alignCellStyle, numberStyle, rowNum);
		
		for (int i = 0; i < COLUMN_SIZE; i++) {
			sheet.autoSizeColumn(i);
		}
		
		return workbook;
	}

	private void createSheetFooter(Sheet sheet, CellStyle alignCellStyle, CellStyle numberStyle, int rowNum) {
		Cell cell;
		Row row;
		row = sheet.createRow(rowNum + 1);
		createCellInSheet(row, 0, "SUM", alignCellStyle);
		
		cell = row.createCell(1);
		
		String sumFormula = createSumFormula(9, rowNum, 'B');
		cell.setCellFormula(sumFormula);
		cell.setCellStyle(numberStyle);
		
		cell = row.createCell(2);
		
		sumFormula = createSumFormula(9, rowNum, 'C');
		cell.setCellFormula(sumFormula);
		cell.setCellStyle(numberStyle);
	}

	private int createSheetBody(WorklogHandler worklogResource, LocalDate from, LocalDate to, Sheet sheet,
			CellStyle alignCellStyle, CellStyle numberStyle, int rowNum) {
		Row row;
		LocalDate instant = from;
		
		while(!instant.isAfter(to)) {
			row = sheet.createRow(rowNum);
			Integer seconds = worklogResource.getWorklogHours().get(instant);
			String date = instant.getDayOfMonth() + " " + 
					instant.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toString();
			
			createCellInSheet(row, 0, date, alignCellStyle);
			
			if (seconds != null) {
				double hours = convertToHour(seconds);
				createCellInSheet(row, 1, hours, numberStyle);
			}
			
			instant = instant.plusDays(1);
			rowNum ++;
		}
		return rowNum;
	}

	private int createSheetTitle(String username, LocalDate from, Sheet sheet, CellStyle headerCellStyle,
			CellStyle alignCellStyle, int rowNum) {
		Row row;
		row = sheet.createRow(rowNum);
		String companyName = "TARGA INFOMOBILITY SRL";
		createCellInSheet(row, 0, companyName, headerCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, (COLUMN_SIZE -1)));
		
		rowNum ++;
		
		row = sheet.createRow(rowNum);
		String companyAddress = "Via Reginato 87/H - 31100 Treviso";
		createCellInSheet(row, 0, companyAddress, alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, (COLUMN_SIZE -1)));
		
		rowNum += 2;
		
		row = sheet.createRow(rowNum);
		createCellInSheet(row, 0, "DIPENDENTE", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 6));
		
		createCellInSheet(row, 4, "PERIODO", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 2));
		
		rowNum ++;
		
		row = sheet.createRow(rowNum);
		createCellInSheet(row, 0, username, alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(5, 5, 4, 6));
		
		String period = from.getMonth().toString() + " " + from.getYear();
		createCellInSheet(row, 4, period, alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 2));
		
		rowNum += 2;
		
		row = sheet.createRow(rowNum);
		createCellInSheet(row, 0, "GIORNO", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(7, 8, 0, 0));
		
		createCellInSheet(row, 1, "PRESENZE", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 2));
		
		createCellInSheet(row, 3, "ORE FEST", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(7, 8, 3, 3));
		
		createCellInSheet(row, 4, "ASSENZE", alignCellStyle);
		sheet.addMergedRegion(new CellRangeAddress(7, 7, 4, (COLUMN_SIZE -1)));
		
		rowNum ++;
		
		row = sheet.createRow(rowNum);
		createCellInSheet(row, 1, "Lav.Ordinario", alignCellStyle);

		createCellInSheet(row, 2, "Lav.Straord", alignCellStyle);
		
		createCellInSheet(row, 4, "FERIE", alignCellStyle);
		
		createCellInSheet(row, 5, "PERMESSI", alignCellStyle);
		
		createCellInSheet(row, 6, "MALATTIA", alignCellStyle);
		
		rowNum ++;
		return rowNum;
	}
	
	private double convertToHour(Integer seconds) {
		return seconds / (60.0 * 60);
	}

	private String createSumFormula(int startRow, int endRow, char column) {
		return "SUM(" + column + startRow + ":" + column + endRow + ")";
	}

	private Cell createCellInSheet(Row row, int column, Double number, CellStyle style) {
		
		Cell cell = row.createCell(column);
		if (number != null) {
			cell.setCellValue(number);
		}
		cell.setCellStyle(style);
		
		return cell;
	}
	
	private Cell createCellInSheet(Row row, int column, String value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}
}
