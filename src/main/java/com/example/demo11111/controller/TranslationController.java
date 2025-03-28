package com.example.demo11111.controller;

import com.example.demo11111.dto.BulkTranslationRequest;
import com.example.demo11111.response.TranslationResponse;
import com.example.demo11111.service.TranslationService;
import com.example.demo11111.model.Translation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @PostMapping("/bulk")
    public ResponseEntity<List<Translation>> translateBulk(@RequestBody BulkTranslationRequest request) {
        List<Translation> translations = translationService.translateBulk(request);
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/text")
    public ResponseEntity<?> translateText(
            @RequestParam String text,
            @RequestParam String sourceLang,
            @RequestParam String targetLang) {
        try {
            Translation translation = translationService.translateAndSave(text, sourceLang, targetLang);
            TranslationResponse response = new TranslationResponse(
                    translation.getOriginalText(),
                    translation.getTranslatedText(),
                    translation.getSourceLang(),
                    translation.getTargetLang());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/{translationId}/users")
    public ResponseEntity<?> getUsersByTranslationId(@PathVariable Integer translationId) {
        Optional<Translation> translation = translationService.getTranslationById(translationId);
        if (translation.isPresent()) {
            return ResponseEntity.ok(translation.get().getUsers());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Перевод с ID " + translationId + " не найден");
        }
    }

    @GetMapping("/all")
    public List<Translation> getAllTranslations() {
        return translationService.getAllTranslations();
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getTranslationById(@PathVariable Integer id) {
        Optional<Translation> translation = translationService.getTranslationById(id);
        if (translation.isPresent()) {
            return ResponseEntity.ok(translation.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Перевод с ID " + id + " не найден");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslationById(@PathVariable Integer id) {
        translationService.deleteTranslationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byTargetLang")
    public ResponseEntity<?> getTranslationsByTargetLang(@RequestParam String targetLang) {
        List<Translation> translations = translationService.getTranslationsByTargetLang(targetLang);
        if (translations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Переводы для языка " + targetLang + " не найдены");
        }
        return ResponseEntity.ok(translations);
    }

}