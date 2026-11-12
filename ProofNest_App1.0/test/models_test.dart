import 'package:flutter_test/flutter_test.dart';
import 'package:proofnest/core/format.dart';
import 'package:proofnest/models/models.dart';

void main() {
  group('model parsing', () {
    test('property with tenants', () {
      final property = Property.fromJson({
        'id': 7,
        'name': 'Maple Court',
        'address': '1 Test Street',
        'propertyType': 'APARTMENT',
        'propertyTypeLabel': 'Apartment',
        'unitCount': 12,
        'status': 'ACTIVE',
        'statusLabel': 'Active',
        'hasImage': true,
        'ownerId': 1,
        'ownerName': 'Sam Taylor',
        'owner': true,
        'inspectionCount': 2,
        'tenants': [
          {
            'id': 5,
            'fullName': 'Alex Morgan',
            'email': 'alex@example.com',
            'addedAt': '2026-10-01T10:00:00+08:00'
          },
        ],
        'createdAt': '2026-09-01T09:00:00+08:00',
        'updatedAt': '2026-09-02T09:00:00+08:00',
      });

      expect(property.id, 7);
      expect(property.owner, isTrue);
      expect(property.hasImage, isTrue);
      expect(property.tenants.single.email, 'alex@example.com');
    });

    test('missing optional fields do not crash', () {
      final property = Property.fromJson({'id': 1, 'name': 'Bare'});
      expect(property.address, isNull);
      expect(property.tenants, isEmpty);
      expect(property.owner, isFalse);
    });

    test('inspection detail with rooms and evidence', () {
      final detail = InspectionDetail.fromJson({
        'inspection': {
          'id': 3,
          'propertyId': 7,
          'propertyName': 'Maple Court',
          'type': 'MOVE_IN',
          'typeLabel': 'Move-in',
          'status': 'DRAFT',
          'statusLabel': 'Draft',
          'inspectionDate': '2026-10-10',
          'notes': null,
          'areaCount': 1,
          'createdByName': 'Sam Taylor',
        },
        'owner': true,
        'evidenceCount': 1,
        'areas': [
          {
            'id': 11,
            'name': 'Kitchen',
            'notes': 'Clean',
            'evidence': [
              {
                'id': 90,
                'originalFileName': 'IMG_1.jpg',
                'contentType': 'image/jpeg',
                'fileSize': 2048,
                'sha256': 'abcdef0123456789abcdef',
                'uploadedById': 5,
                'uploadedByName': 'Alex Morgan',
                'uploadedAt': '2026-10-10T17:34:00+08:00',
                'canDelete': true,
              },
            ],
          },
        ],
      });

      expect(detail.inspection.isMoveIn, isTrue);
      expect(detail.inspection.dateLabel, 'Oct 10, 2026');
      expect(detail.inspection.notes, isNull);
      expect(detail.areas.single.evidence.single.shortHash, 'abcdef012345…');
      expect(detail.areas.single.evidence.single.canDelete, isTrue);
    });

    test('comparison keeps rooms that exist on one side only', () {
      final comparison = Comparison.fromJson({
        'propertyId': 7,
        'first': {'id': 1, 'type': 'MOVE_IN', 'inspectionDate': '2025-10-01'},
        'second': {'id': 2, 'type': 'MOVE_OUT', 'inspectionDate': '2026-10-01'},
        'pairs': [
          {
            'name': 'Kitchen',
            'first': {
              'id': 1,
              'name': 'Kitchen',
              'notes': null,
              'evidence': []
            },
            'second': null,
            'comments': [
              {
                'id': 4,
                'authorId': 5,
                'authorName': 'Alex Morgan',
                'authorRole': 'TENANT',
                'message': 'Looks the same.',
                'createdAt': '2026-10-02T08:00:00+08:00',
              },
            ],
          },
        ],
      });

      final pair = comparison.pairs.single;
      expect(pair.first, isNotNull);
      expect(pair.second, isNull);
      expect(pair.comments.single.roleLabel, 'Tenant');
    });

    test('finding action flags are parsed', () {
      final finding = Finding.fromJson({
        'id': 9,
        'title': 'Scratch',
        'description': 'New scratch',
        'areaName': 'Kitchen',
        'status': 'OPEN',
        'statusLabel': 'Open',
        'canDispute': true,
        'canResolve': false,
      });
      expect(finding.canDispute, isTrue);
      expect(finding.canResolve, isFalse);
    });

    test('auth result and user role', () {
      final result = AuthResult.fromJson({
        'token': 'abc',
        'expiresAt': '2026-12-01T00:00:00+08:00',
        'user': {
          'id': 1,
          'fullName': 'Sam Taylor',
          'email': 'sam@example.com',
          'role': 'LANDLORD'
        },
      });
      expect(result.token, 'abc');
      expect(result.user.isLandlord, isTrue);
      expect(result.user.roleLabel, 'Landlord');
    });
  });

  group('formatting helpers', () {
    test('dates', () {
      expect(formatDate(DateTime(2026, 1, 5)), 'Jan 5, 2026');
      expect(formatDate(null), '');
      expect(dayToIso(DateTime(2026, 3, 4)), '2026-03-04');
      expect(parseDay('not a date'), isNull);
      expect(parseDay('2026-10-10'), DateTime(2026, 10, 10));
    });

    test('server address normalisation', () {
      expect(normalizeBaseUrl('192.168.1.20:8080'), 'http://192.168.1.20:8080');
      expect(normalizeBaseUrl('https://proofnest.example.com/'),
          'https://proofnest.example.com');
      expect(normalizeBaseUrl('  '), '');
    });

    test('names and sizes', () {
      expect(initials('Sam Taylor'), 'ST');
      expect(initials('Sam'), 'S');
      expect(initials(''), '?');
      expect(firstName('  Alex   Morgan '), 'Alex');
      expect(formatBytes(512), '512 B');
      expect(formatBytes(2048), '2 KB');
      expect(formatBytes(5 * 1024 * 1024), '5.0 MB');
    });
  });
}
