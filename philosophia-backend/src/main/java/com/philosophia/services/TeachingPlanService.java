package com.philosophia.services;

import com.philosophia.dto.chapter.CreateChapterRequest;
import com.philosophia.dto.scheduling.TeachingPlanResponse;
import com.philosophia.dto.chapter.UpdateChapterRequest;
import com.philosophia.enums.TeachingPlanStatus;
import com.philosophia.exceptions.InvalidTeachingPlanException;
import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.models.Chapter;
import com.philosophia.models.TeachingPlan;
import com.philosophia.repository.ChapterRepository;
import com.philosophia.repository.StudentRepository;
import com.philosophia.repository.TeachingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeachingPlanService {

    private final ChapterRepository chapterRepository;
    private final TeachingPlanRepository teachingPlanRepository;
    private final StudentRepository studentRepository;

    public TeachingPlanService(ChapterRepository chapterRepository,
                               TeachingPlanRepository teachingPlanRepository,
                               StudentRepository studentRepository) {
        this.chapterRepository = chapterRepository;
        this.teachingPlanRepository = teachingPlanRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public TeachingPlanResponse createChapter(CreateChapterRequest req) {
        validate(req);

        Chapter chapter = new Chapter();
        chapter.setName(req.chapterName());
        chapter.setType(req.type());
        chapterRepository.save(chapter);

        TeachingPlan plan = new TeachingPlan();
        plan.setChapter(chapter);
        plan.setSection(req.section());
        plan.setDurationWeeks(req.durationWeeks());
        plan.setSessionsPerWeek(req.sessionsPerWeek());
        plan.setMaxStudents(req.maxStudents());
        plan.setStartDate(req.startDate());
        plan.setStatus(TeachingPlanStatus.DRAFT);
        teachingPlanRepository.save(plan);

        int enrolledCount = studentRepository.findBySection(req.section()).size();

        return toResponse(plan, enrolledCount);
    }

    @Transactional(readOnly = true)
    public List<TeachingPlanResponse> getAllTeachingPlans() {
        return teachingPlanRepository.findAll().stream()
                .map(plan -> toResponse(plan, studentRepository.findBySection(plan.getSection()).size()))
                .toList();
    }

    private void validate(CreateChapterRequest req) {
        if (req.chapterName() == null || req.chapterName().isBlank()) {
            throw new InvalidTeachingPlanException("Le nom du chapitre est requis.");
        }
        if (req.type() == null) {
            throw new InvalidTeachingPlanException("Le type (cours/TP) est requis.");
        }
        if (req.section() == null) {
            throw new InvalidTeachingPlanException("La section cible est requise.");
        }
        if (req.durationWeeks() == null || req.durationWeeks() <= 0) {
            throw new InvalidTeachingPlanException("La durée en semaines doit être positive.");
        }
        if (req.sessionsPerWeek() == null || req.sessionsPerWeek() <= 0) {
            throw new InvalidTeachingPlanException("Le nombre de séances par semaine doit être positif.");
        }
        if (req.maxStudents() == null || req.maxStudents() <= 0) {
            throw new InvalidTeachingPlanException("Le nombre maximum d'élèves doit être positif.");
        }
        if (req.startDate() == null) {
            throw new InvalidTeachingPlanException("La date de début est requise.");
        }
    }

    private TeachingPlanResponse toResponse(TeachingPlan plan, int enrolledCount) {
        return new TeachingPlanResponse(
                plan.getId(),
                plan.getChapter().getName(),
                plan.getChapter().getType().name(),
                plan.getSection().name(),
                plan.getDurationWeeks(),
                plan.getSessionsPerWeek(),
                plan.getMaxStudents(),
                plan.getStartDate(),
                plan.getStatus().name(),
                enrolledCount
        );
    }
    @Transactional
    public TeachingPlanResponse updateChapter(Long teachingPlanId, UpdateChapterRequest req) {
        validateUpdate(req);

        TeachingPlan plan = teachingPlanRepository.findById(teachingPlanId)
                .orElseThrow(() -> new UserNotFoundException(teachingPlanId));

        Chapter chapter = plan.getChapter();
        chapter.setName(req.chapterName());
        chapter.setType(req.type());
        chapterRepository.save(chapter);

        plan.setSection(req.section());
        plan.setDurationWeeks(req.durationWeeks());
        plan.setSessionsPerWeek(req.sessionsPerWeek());
        plan.setMaxStudents(req.maxStudents());
        plan.setStartDate(req.startDate());
        teachingPlanRepository.save(plan);

        int enrolledCount = studentRepository.findBySection(req.section()).size();

        return toResponse(plan, enrolledCount);
    }

    @Transactional(readOnly = true)
    public TeachingPlanResponse getChapterById(Long teachingPlanId) {
        TeachingPlan plan = teachingPlanRepository.findById(teachingPlanId)
                .orElseThrow(() -> new UserNotFoundException(teachingPlanId));
        return toResponse(plan, studentRepository.findBySection(plan.getSection()).size());
    }

    private void validateUpdate(UpdateChapterRequest req) {
        if (req.chapterName() == null || req.chapterName().isBlank()) {
            throw new InvalidTeachingPlanException("Le nom du chapitre est requis.");
        }
        if (req.type() == null) {
            throw new InvalidTeachingPlanException("Le type (cours/TP) est requis.");
        }
        if (req.section() == null) {
            throw new InvalidTeachingPlanException("La section cible est requise.");
        }
        if (req.durationWeeks() == null || req.durationWeeks() <= 0) {
            throw new InvalidTeachingPlanException("La durée en semaines doit être positive.");
        }
        if (req.sessionsPerWeek() == null || req.sessionsPerWeek() <= 0) {
            throw new InvalidTeachingPlanException("Le nombre de séances par semaine doit être positif.");
        }
        if (req.maxStudents() == null || req.maxStudents() <= 0) {
            throw new InvalidTeachingPlanException("Le nombre maximum d'élèves doit être positif.");
        }
        if (req.startDate() == null) {
            throw new InvalidTeachingPlanException("La date de début est requise.");
        }
    }
}