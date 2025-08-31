package com.financetracker.controller;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.service.TransactionService;
import com.financetracker.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addTransaction(@RequestParam String description,
                                              @RequestParam double amount,
                                              @RequestParam String type,
                                              @RequestParam String category,
                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }

            User user = userService.getUserById(userId);
            Transaction transaction = transactionService.addTransaction(description, amount, type, category, user);

            response.put("success", true);
            response.put("transaction", transaction);
            response.put("totalIncome", transactionService.getTotalIncome(user));
            response.put("totalExpense", transactionService.getTotalExpense(user));
            response.put("savings", transactionService.getSavings(user));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteTransaction(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }

            User user = userService.getUserById(userId);
            transactionService.deleteTransaction(id, user);

            response.put("success", true);
            response.put("totalIncome", transactionService.getTotalIncome(user));
            response.put("totalExpense", transactionService.getTotalExpense(user));
            response.put("savings", transactionService.getSavings(user));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/all")
    @ResponseBody
    public Map<String, Object> getAllTransactions(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }

            User user = userService.getUserById(userId);
            List<Transaction> transactions = transactionService.getAllTransactions(user);

            response.put("success", true);
            response.put("transactions", transactions);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response, HttpSession session) throws IOException, DocumentException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = userService.getUserById(userId);
        List<Transaction> transactions = transactionService.getAllTransactions(user);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Transactions", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        document.add(new Paragraph("Generated on: " + new Date().toString()));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.addCell("Description");
        table.addCell("Amount");
        table.addCell("Type");
        table.addCell("Category");
        table.addCell("Date");

        for (Transaction t : transactions) {
            table.addCell(t.getDescription());
            table.addCell(String.valueOf(t.getAmount()));
            table.addCell(t.getType());
            table.addCell(t.getCategory());
            table.addCell(t.getDate().toString());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = userService.getUserById(userId);
        List<Transaction> transactions = transactionService.getAllTransactions(user);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Description");
        header.createCell(1).setCellValue("Amount");
        header.createCell(2).setCellValue("Type");
        header.createCell(3).setCellValue("Category");
        header.createCell(4).setCellValue("Date");

        int rowNum = 1;
        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getDescription());
            row.createCell(1).setCellValue(t.getAmount());
            row.createCell(2).setCellValue(t.getType());
            row.createCell(3).setCellValue(t.getCategory());
            row.createCell(4).setCellValue(t.getDate().toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

