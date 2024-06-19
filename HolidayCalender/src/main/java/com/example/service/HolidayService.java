package com.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Holiday;
import com.example.repo.HolidayRepository;

@Service

public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Value("${UPLOAD_DIR}")
    private String UPLOAD_DIR;


    public String saveHoliday(LocalDate fullDate, String description, String holidayType, MultipartFile file) throws Exception {
        if(holidayRepository.isFullDateExists(fullDate)) return " Already Exists";

        Holiday holiday = new Holiday();
        holiday.setFullDate(fullDate);
        holiday.setDescription(description);
        holiday.setHolidayType(holidayType);

        Holiday savedHoliday=holidayRepository.save(holiday);
        String fileName =fullDate.toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        savedHoliday.setFilePath(filePath.toString());

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        holidayRepository.save(savedHoliday);
        return "Successfully Added";

    }


    public List<Holiday> getAllHolidays() {
        List<Holiday> holidays = holidayRepository.findAll();
        for (Holiday holiday : holidays) {
            String filePath = holiday.getFilePath();
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                holiday.setFileBase64(encodedString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return holidays;
    }

    public Optional<Holiday> getHolidayById(Long id) {
        return holidayRepository.findById(id);
    }
}
