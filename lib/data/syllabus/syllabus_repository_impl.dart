import '../../domain/syllabus/chapter.dart';

class SyllabusRepositoryImpl {
  final List<Chapter> _chapters = [];

  Future<List<Chapter>> getAllChapters() async {
    return List.unmodifiable(_chapters);
  }

  Future<List<Chapter>> getChaptersBySubject(String subjectId) async {
    return _chapters.where((c) => c.subjectId == subjectId).toList();
  }

  Future<void> updateChapter(Chapter chapter) async {
    final idx = _chapters.indexWhere((c) => c.id == chapter.id);
    if (idx != -1) {
      _chapters[idx] = chapter;
    }
  }
}
