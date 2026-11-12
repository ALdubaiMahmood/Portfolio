import '../core/format.dart';

// ---------------------------------------------------------------------------
// Small parsing helpers. They tolerate missing fields so a newer server that
// adds or omits a field does not crash the app.
// ---------------------------------------------------------------------------

int _int(dynamic v) => v is num ? v.toInt() : (int.tryParse('$v') ?? 0);
String _str(dynamic v) => v == null ? '' : '$v';
String? _strOrNull(dynamic v) => v == null ? null : '$v';
bool _bool(dynamic v) => v == true;
DateTime? _time(dynamic v) => v == null ? null : DateTime.tryParse('$v');

List<Map<String, dynamic>> _maps(dynamic v) {
  if (v is List) {
    return v.whereType<Map<String, dynamic>>().toList();
  }
  return const [];
}

Map<String, dynamic> _map(dynamic v) =>
    v is Map<String, dynamic> ? v : const <String, dynamic>{};

// ---------------------------------------------------------------------------
// Choices shown in the UI. `value` is what the server expects.
// ---------------------------------------------------------------------------

class Choice {
  const Choice(this.value, this.label);

  final String value;
  final String label;
}

const List<Choice> kPropertyTypes = [
  Choice('APARTMENT', 'Apartment'),
  Choice('HOUSE', 'House'),
  Choice('COMMERCIAL', 'Commercial'),
  Choice('OTHER', 'Other'),
];

const List<Choice> kPropertyStatuses = [
  Choice('ACTIVE', 'Active'),
  Choice('MAINTENANCE', 'Maintenance'),
  Choice('ARCHIVED', 'Archived'),
];

const List<Choice> kInspectionTypes = [
  Choice('MOVE_IN', 'Move-in'),
  Choice('MOVE_OUT', 'Move-out'),
  Choice('ROUTINE', 'Routine'),
  Choice('OTHER', 'Other'),
];

const List<Choice> kInspectionStatuses = [
  Choice('DRAFT', 'Draft'),
  Choice('IN_PROGRESS', 'In progress'),
  Choice('COMPLETED', 'Completed'),
];

const List<Choice> kFindingStatuses = [
  Choice('OPEN', 'Open'),
  Choice('DISPUTED', 'Disputed'),
  Choice('RESOLVED', 'Resolved'),
];

// ---------------------------------------------------------------------------
// Users
// ---------------------------------------------------------------------------

class AppUser {
  const AppUser(
      {required this.id,
      required this.fullName,
      required this.email,
      required this.role});

  factory AppUser.fromJson(Map<String, dynamic> json) => AppUser(
        id: _int(json['id']),
        fullName: _str(json['fullName']),
        email: _str(json['email']),
        role: _str(json['role']),
      );

  final int id;
  final String fullName;
  final String email;
  final String role;

  bool get isLandlord => role == 'LANDLORD';
  String get roleLabel => isLandlord ? 'Landlord' : 'Tenant';
}

class AuthResult {
  const AuthResult({required this.token, required this.user});

  factory AuthResult.fromJson(Map<String, dynamic> json) => AuthResult(
        token: _str(json['token']),
        user: AppUser.fromJson(_map(json['user'])),
      );

  final String token;
  final AppUser user;
}

// ---------------------------------------------------------------------------
// Properties
// ---------------------------------------------------------------------------

class Tenant {
  const Tenant({required this.id, required this.fullName, required this.email});

  factory Tenant.fromJson(Map<String, dynamic> json) => Tenant(
        id: _int(json['id']),
        fullName: _str(json['fullName']),
        email: _str(json['email']),
      );

  final int id;
  final String fullName;
  final String email;
}

class Property {
  const Property({
    required this.id,
    required this.name,
    required this.address,
    required this.propertyType,
    required this.propertyTypeLabel,
    required this.unitCount,
    required this.status,
    required this.statusLabel,
    required this.hasImage,
    required this.ownerName,
    required this.owner,
    required this.inspectionCount,
    required this.tenants,
  });

  factory Property.fromJson(Map<String, dynamic> json) => Property(
        id: _int(json['id']),
        name: _str(json['name']),
        address: _strOrNull(json['address']),
        propertyType: _str(json['propertyType']),
        propertyTypeLabel: _str(json['propertyTypeLabel']),
        unitCount: _int(json['unitCount']),
        status: _str(json['status']),
        statusLabel: _str(json['statusLabel']),
        hasImage: _bool(json['hasImage']),
        ownerName: _str(json['ownerName']),
        owner: _bool(json['owner']),
        inspectionCount: _int(json['inspectionCount']),
        tenants: _maps(json['tenants']).map(Tenant.fromJson).toList(),
      );

  final int id;
  final String name;
  final String? address;
  final String propertyType;
  final String propertyTypeLabel;
  final int unitCount;
  final String status;
  final String statusLabel;
  final bool hasImage;
  final String ownerName;

  /// True when the signed-in user owns this property (landlord actions are available).
  final bool owner;
  final int inspectionCount;
  final List<Tenant> tenants;
}

// ---------------------------------------------------------------------------
// Inspections
// ---------------------------------------------------------------------------

class InspectionSummary {
  const InspectionSummary({
    required this.id,
    required this.propertyId,
    required this.propertyName,
    required this.type,
    required this.typeLabel,
    required this.status,
    required this.statusLabel,
    required this.inspectionDate,
    required this.notes,
    required this.areaCount,
    required this.createdByName,
  });

  factory InspectionSummary.fromJson(Map<String, dynamic> json) =>
      InspectionSummary(
        id: _int(json['id']),
        propertyId: _int(json['propertyId']),
        propertyName: _str(json['propertyName']),
        type: _str(json['type']),
        typeLabel: _str(json['typeLabel']),
        status: _str(json['status']),
        statusLabel: _str(json['statusLabel']),
        inspectionDate: _str(json['inspectionDate']),
        notes: _strOrNull(json['notes']),
        areaCount: _int(json['areaCount']),
        createdByName: _str(json['createdByName']),
      );

  final int id;
  final int propertyId;
  final String propertyName;
  final String type;
  final String typeLabel;
  final String status;
  final String statusLabel;
  final String inspectionDate;
  final String? notes;
  final int areaCount;
  final String createdByName;

  DateTime? get date => parseDay(inspectionDate);
  String get dateLabel => formatDate(date);
  bool get isMoveIn => type == 'MOVE_IN';
  bool get isMoveOut => type == 'MOVE_OUT';
}

class Evidence {
  const Evidence({
    required this.id,
    required this.originalFileName,
    required this.fileSize,
    required this.sha256,
    required this.uploadedByName,
    required this.uploadedAt,
    required this.canDelete,
  });

  factory Evidence.fromJson(Map<String, dynamic> json) => Evidence(
        id: _int(json['id']),
        originalFileName: _str(json['originalFileName']),
        fileSize: _int(json['fileSize']),
        sha256: _str(json['sha256']),
        uploadedByName: _str(json['uploadedByName']),
        uploadedAt: _time(json['uploadedAt']),
        canDelete: _bool(json['canDelete']),
      );

  final int id;
  final String originalFileName;
  final int fileSize;
  final String sha256;
  final String uploadedByName;
  final DateTime? uploadedAt;
  final bool canDelete;

  /// The BRD shows only part of the hash next to each photo.
  String get shortHash =>
      sha256.length > 12 ? '${sha256.substring(0, 12)}…' : sha256;
}

class InspectionArea {
  const InspectionArea(
      {required this.id,
      required this.name,
      required this.notes,
      required this.evidence});

  factory InspectionArea.fromJson(Map<String, dynamic> json) => InspectionArea(
        id: _int(json['id']),
        name: _str(json['name']),
        notes: _strOrNull(json['notes']),
        evidence: _maps(json['evidence']).map(Evidence.fromJson).toList(),
      );

  final int id;
  final String name;
  final String? notes;
  final List<Evidence> evidence;
}

class InspectionDetail {
  const InspectionDetail({
    required this.inspection,
    required this.owner,
    required this.evidenceCount,
    required this.areas,
  });

  factory InspectionDetail.fromJson(Map<String, dynamic> json) =>
      InspectionDetail(
        inspection: InspectionSummary.fromJson(_map(json['inspection'])),
        owner: _bool(json['owner']),
        evidenceCount: _int(json['evidenceCount']),
        areas: _maps(json['areas']).map(InspectionArea.fromJson).toList(),
      );

  final InspectionSummary inspection;
  final bool owner;
  final int evidenceCount;
  final List<InspectionArea> areas;
}

// ---------------------------------------------------------------------------
// Comparison
// ---------------------------------------------------------------------------

class Comment {
  const Comment({
    required this.id,
    required this.authorName,
    required this.authorRole,
    required this.message,
    required this.createdAt,
  });

  factory Comment.fromJson(Map<String, dynamic> json) => Comment(
        id: _int(json['id']),
        authorName: _str(json['authorName']),
        authorRole: _str(json['authorRole']),
        message: _str(json['message']),
        createdAt: _time(json['createdAt']),
      );

  final int id;
  final String authorName;
  final String authorRole;
  final String message;
  final DateTime? createdAt;

  String get roleLabel => authorRole == 'LANDLORD' ? 'Landlord' : 'Tenant';
}

/// One room matched by name. [first] (move-in) or [second] (move-out) is null when that inspection has no such room.
class AreaPair {
  const AreaPair(
      {required this.name,
      required this.first,
      required this.second,
      required this.comments});

  factory AreaPair.fromJson(Map<String, dynamic> json) => AreaPair(
        name: _str(json['name']),
        first: json['first'] is Map<String, dynamic>
            ? InspectionArea.fromJson(_map(json['first']))
            : null,
        second: json['second'] is Map<String, dynamic>
            ? InspectionArea.fromJson(_map(json['second']))
            : null,
        comments: _maps(json['comments']).map(Comment.fromJson).toList(),
      );

  final String name;
  final InspectionArea? first;
  final InspectionArea? second;
  final List<Comment> comments;
}

class Comparison {
  const Comparison(
      {required this.propertyId,
      required this.first,
      required this.second,
      required this.pairs});

  factory Comparison.fromJson(Map<String, dynamic> json) => Comparison(
        propertyId: _int(json['propertyId']),
        first: InspectionSummary.fromJson(_map(json['first'])),
        second: InspectionSummary.fromJson(_map(json['second'])),
        pairs: _maps(json['pairs']).map(AreaPair.fromJson).toList(),
      );

  final int propertyId;
  final InspectionSummary first;
  final InspectionSummary second;
  final List<AreaPair> pairs;
}

// ---------------------------------------------------------------------------
// Findings
// ---------------------------------------------------------------------------

class Finding {
  const Finding({
    required this.id,
    required this.propertyName,
    required this.areaName,
    required this.title,
    required this.description,
    required this.status,
    required this.statusLabel,
    required this.createdByName,
    required this.canDispute,
    required this.canResolve,
    required this.createdAt,
  });

  factory Finding.fromJson(Map<String, dynamic> json) => Finding(
        id: _int(json['id']),
        propertyName: _str(json['propertyName']),
        areaName: _str(json['areaName']),
        title: _str(json['title']),
        description: _str(json['description']),
        status: _str(json['status']),
        statusLabel: _str(json['statusLabel']),
        createdByName: _str(json['createdByName']),
        canDispute: _bool(json['canDispute']),
        canResolve: _bool(json['canResolve']),
        createdAt: _time(json['createdAt']),
      );

  final int id;
  final String propertyName;
  final String areaName;
  final String title;
  final String description;
  final String status;
  final String statusLabel;
  final String createdByName;

  /// Server-computed: true only when the signed-in user may dispute / resolve right now.
  final bool canDispute;
  final bool canResolve;
  final DateTime? createdAt;
}

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

class Dashboard {
  const Dashboard({
    required this.landlord,
    required this.propertyCount,
    required this.inspectionCount,
    required this.draftCount,
    required this.inProgressCount,
    required this.completedCount,
    required this.openFindingCount,
    required this.disputedFindingCount,
    required this.recentInspections,
  });

  factory Dashboard.fromJson(Map<String, dynamic> json) => Dashboard(
        landlord: _bool(json['landlord']),
        propertyCount: _int(json['propertyCount']),
        inspectionCount: _int(json['inspectionCount']),
        draftCount: _int(json['draftCount']),
        inProgressCount: _int(json['inProgressCount']),
        completedCount: _int(json['completedCount']),
        openFindingCount: _int(json['openFindingCount']),
        disputedFindingCount: _int(json['disputedFindingCount']),
        recentInspections: _maps(json['recentInspections'])
            .map(InspectionSummary.fromJson)
            .toList(),
      );

  final bool landlord;
  final int propertyCount;
  final int inspectionCount;
  final int draftCount;
  final int inProgressCount;
  final int completedCount;
  final int openFindingCount;
  final int disputedFindingCount;
  final List<InspectionSummary> recentInspections;
}
