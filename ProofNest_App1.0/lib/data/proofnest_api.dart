import '../core/api_exception.dart';
import '../models/models.dart';

/// Temporary local-only data provider. Connect Firebase before enabling shared data operations.
class ProofnestApi {
  ProofnestApi({required bool Function() isLandlord})
      : _isLandlord = isLandlord;

  final bool Function() _isLandlord;

  Future<Dashboard> dashboard() async => Dashboard(
        landlord: _isLandlord(),
        propertyCount: 0,
        inspectionCount: 0,
        draftCount: 0,
        inProgressCount: 0,
        completedCount: 0,
        openFindingCount: 0,
        disputedFindingCount: 0,
        recentInspections: const [],
      );

  Future<List<Property>> properties({String? query, String? status}) async =>
      const [];

  Future<Property> property(int id) => _requiresFirebase<Property>();

  Future<Property> saveProperty({
    int? id,
    required String name,
    required String address,
    required String propertyType,
    required int unitCount,
    required String status,
    String? imagePath,
  }) =>
      _requiresFirebase<Property>();

  Future<void> deleteProperty(int id) => _requiresFirebase<void>();

  Future<Property> addTenant(int propertyId, String email) =>
      _requiresFirebase<Property>();

  Future<List<InspectionSummary>> propertyInspections(int propertyId) async =>
      const [];

  String propertyImageUrl(int id) => '';

  Future<List<InspectionSummary>> inspections(
          {String? query, String? status}) async =>
      const [];

  Future<InspectionDetail> inspection(int id) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> createInspection({
    required int propertyId,
    required String type,
    required String inspectionDate,
    String? notes,
  }) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> updateInspection(
    int id, {
    required String type,
    required String inspectionDate,
    String? notes,
  }) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> setInspectionStatus(int id, String status) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> addArea(
          int inspectionId, String name, String? notes) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> updateArea(
          int inspectionId, int areaId, String name, String? notes) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> uploadEvidence(
          int inspectionId, int areaId, String imagePath) =>
      _requiresFirebase<InspectionDetail>();

  Future<InspectionDetail> deleteEvidence(int inspectionId, int evidenceId) =>
      _requiresFirebase<InspectionDetail>();

  String evidenceUrl(int evidenceId) => '';

  Map<String, String> get imageHeaders => const {};

  Future<Comparison> comparison(int propertyId, int moveInId, int moveOutId) =>
      _requiresFirebase<Comparison>();

  Future<Comment> addComment({
    required int propertyId,
    required int moveInId,
    required int moveOutId,
    required String areaName,
    required String message,
  }) =>
      _requiresFirebase<Comment>();

  Future<List<Finding>> findings({String? status}) async => const [];

  Future<Finding> createFinding({
    required int propertyId,
    required int moveInId,
    required int moveOutId,
    required String areaName,
    required String title,
    required String description,
  }) =>
      _requiresFirebase<Finding>();

  Future<Finding> setFindingStatus(int id, String status) =>
      _requiresFirebase<Finding>();

  Future<T> _requiresFirebase<T>() async {
    throw ApiException(
        'Shared property and inspection features are not connected yet. Add Firebase to use them.');
  }
}
