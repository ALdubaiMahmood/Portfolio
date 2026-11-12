# ProofNest — Business Requirements Document (BRD)

**Document version:** 2.0  
**Document date:** 4 November 2026  
**Project type:** Individual Software Engineering graduation project  
**Product format:** Android-first mobile application  
**Document status:** Updated scope — draft for supervisor review  
**Project tagline:** *Every Detail. Every Proof. One Place.*

---

## Document control

| Item | Details |
|---|---|
| Project name | ProofNest |
| Product | Mobile rental inspection, communication, evidence, and tenancy-record platform |
| Primary users | Landlords/property owners and tenants |
| Frontend | Flutter and Dart |
| Backend | None in the current prototype; future shared-service approach is undecided |
| Database | None in the current prototype; Firebase is a candidate for later selection |
| Evidence storage | Not implemented; future private storage approach to be selected |
| Initial target | Android application implemented in Flutter/Dart |
| Development approach | Individual, iterative development with a working end-to-end workflow delivered before advanced refinements |
| Previous direction | Thymeleaf web interface |
| Updated direction | Flutter/Dart-only local prototype for now; Java backend removed on 11 November 2026 |

### Current implementation direction (11 November 2026)

The current app is Flutter/Dart-only. There is no Java service, backend, or database. Registration and sign-in use device-local storage for development; shared property data and cross-device accounts are not available. Firebase Authentication and a Firebase data store are candidates for a future phase, but have not been selected or configured. The future-state requirements below remain product goals and do not describe working features in the current prototype.

### Version history

| Version | Date | Description |
|---|---|---|
| 1.0 | 19 October 2026 | Initial web-based rental inspection and evidence-management BRD |
| 2.0 | 4 November 2026 | Revised for mobile application; added chat, maintenance, notifications, offline work, handover, deposit records, inventory, utility readings, and secure tenant invitations; removed the AI Inspection Assistant |

### Important scope decision

The previously discussed **Feature G — AI Inspection Assistant is excluded** from ProofNest. The application will not perform AI-based damage detection, automatic image interpretation, AI-generated findings, or AI-based responsibility decisions. Inspection comparison and findings will be handled through user review and manual evidence assessment.

---

## Table of contents

1. [Executive summary](#1-executive-summary)
2. [Background and problem statement](#2-background-and-problem-statement)
3. [Product vision and objectives](#3-product-vision-and-objectives)
4. [Scope](#4-scope)
5. [Stakeholders and user roles](#5-stakeholders-and-user-roles)
6. [User journeys](#6-user-journeys)
7. [End-to-end business process](#7-end-to-end-business-process)
8. [Functional requirements](#8-functional-requirements)
9. [Business rules](#9-business-rules)
10. [Mobile application screens and navigation](#10-mobile-application-screens-and-navigation)
11. [Data requirements and conceptual data model](#11-data-requirements-and-conceptual-data-model)
12. [Solution architecture and technology](#12-solution-architecture-and-technology)
13. [Deferred service contract](#13-deferred-service-contract-not-implemented)
14. [Offline operation and synchronization](#14-offline-operation-and-synchronization)
15. [Security, privacy, and evidence integrity](#15-security-privacy-and-evidence-integrity)
16. [Non-functional requirements](#16-non-functional-requirements)
17. [Reports and outputs](#17-reports-and-outputs)
18. [Acceptance criteria and success measures](#18-acceptance-criteria-and-success-measures)
19. [Testing and evaluation plan](#19-testing-and-evaluation-plan)
20. [Development process and eight-week roadmap](#20-development-process-and-eight-week-roadmap)
21. [Risks and mitigations](#21-risks-and-mitigations)
22. [Assumptions, constraints, and dependencies](#22-assumptions-constraints-and-dependencies)
23. [Out of scope](#23-out-of-scope)
24. [Open questions for supervisor review](#24-open-questions-for-supervisor-review)
25. [Glossary](#25-glossary)

---

## 1. Executive summary

ProofNest is an Android-first mobile application designed to help landlords and tenants manage rental-property condition records and communicate throughout a tenancy. It provides a shared place to create and complete move-in and move-out inspections, capture room-by-room photographs and notes, compare evidence, record findings, and preserve each party's separate response.

The updated project also supports day-to-day tenancy coordination. Landlords and tenants can communicate through property-specific chat, report and track maintenance requests, receive reminders, and review a chronological property timeline. At handover, the application can record keys, access cards, appliances, furniture, utility-meter readings, the original deposit amount, and proposed deposit deductions with supporting evidence and responses.

ProofNest is intended to improve clarity, organization, collaboration, and traceability. It does not determine legal liability, decide whether a deposit deduction is lawful, certify the origin of photographs, or replace professional inspection or legal advice.

### 1.1 Product summary

| Area | Description |
|---|---|
| Main problem | Rental condition evidence and tenancy communication can be scattered across personal photo galleries, messaging applications, phone calls, and documents. This makes it harder to compare conditions, follow maintenance issues, and understand what each party agreed or disputed. |
| Proposed solution | One mobile workspace for property records, inspections, evidence, comparison, chat, maintenance, handover, and reports. |
| Main differentiator | A connected record linking property, room, inspection, photo evidence, finding, conversation, maintenance activity, and party response. |
| Primary platform | Flutter mobile application, with Android as the first demonstration target. |
| Backend | Not part of the current local prototype; future shared-service design is pending. |
| Data store | Not connected. Firebase is a candidate for future shared data. |
| Evidence files | Not implemented; future private storage and metadata approach is pending. |
| AI feature | Explicitly excluded. |

### 1.2 Core product promise

**Every important rental inspection, issue, conversation, and handover record should be connected to the correct property and available to the people authorized to see it.**

---

## 2. Background and problem statement

### 2.1 Background

A tenancy involves multiple events: a property is prepared, a tenant moves in, maintenance issues may occur, and the tenant eventually moves out. Property-condition information is often captured at different times by different people. If photographs, notes, messages, and repair updates are stored separately, it can be difficult to reconstruct what happened or compare the original condition with the later condition.

ProofNest addresses this organizational problem by connecting rental records to a property and its tenancy participants. It provides structured workflows rather than relying only on free-form messages or unorganized photo collections.

### 2.2 Problems to solve

| ID | Problem | Effect on users | ProofNest response |
|---|---|---|---|
| P-01 | Move-in and move-out photographs are stored in different places. | Users spend time searching and may overlook relevant evidence. | Store evidence against a property, inspection, room, and checklist item. |
| P-02 | There is no consistent room-by-room inspection process. | Users may forget rooms or important items. | Guided checklists, progress indicators, and missing-evidence reminders. |
| P-03 | Landlord and tenant discussions happen across different channels. | Decisions and explanations become difficult to find. | Property-specific chat with linked inspection and maintenance discussions. |
| P-04 | Findings may be interpreted differently by each party. | Disagreements can remain unclear or undocumented. | Separate comments and accept/dispute responses for each participant. |
| P-05 | Maintenance requests are difficult to follow. | A repair can be forgotten or its status may be unclear. | Maintenance request records, updates, scheduled visits, and completion evidence. |
| P-06 | Important events are not shown in one chronological place. | Users cannot easily understand the tenancy history. | A property timeline built from recorded events. |
| P-07 | Evidence can be replaced or updated without a clear history. | Users may question whether the record changed. | Preserve original evidence references, versions, hashes, and audit events. |
| P-08 | Inspection dates, repairs, and responses can be forgotten. | Tasks may be delayed. | In-app notifications and reminders. |
| P-09 | Move-out handover information is incomplete. | Keys, appliances, inventory, and readings may be disputed or forgotten. | Handover checklist, inventory, and utility-meter records. |
| P-10 | Deposit discussions may lack linked explanations. | Parties may not understand the basis of proposed deductions. | Record the original deposit amount, proposed deductions, evidence, and responses without making legal decisions. |
| P-11 | Inspections may take place where connectivity is weak. | Users may be unable to save notes or photographs. | Local draft capture and later synchronization. |
| P-12 | A user may receive access to a property they should not see. | Private property or conversation information could be exposed. | Property-specific membership and server-side authorization. |

### 2.3 Project opportunity

ProofNest combines inspection evidence and tenancy coordination in a single mobile workflow. Rather than becoming a broad property-rental marketplace, the project remains focused on the condition and history of a rental property and the communication required to manage that condition.

---

## 3. Product vision and objectives

### 3.1 Vision

To provide landlords and tenants with a clear, shared, and traceable mobile workspace for rental inspections and tenancy-related records.

### 3.2 Main objective

Design, implement, and evaluate a mobile application that helps landlords and tenants document property condition, communicate about issues, manage maintenance, and preserve organized inspection and handover records.

### 3.3 Specific objectives

1. Develop a Flutter mobile application with a consistent ProofNest design system and clear navigation.
2. Reuse and extend the existing Spring Boot and MySQL project so the mobile application can securely exchange data through REST APIs.
3. Support accounts, landlord/tenant membership, property creation, and secure tenant invitations.
4. Provide move-in and move-out inspections with room-by-room checklists, photographs, notes, and inspection progress.
5. Allow users to compare inspection evidence and record findings.
6. Preserve landlord and tenant comments and responses separately.
7. Provide property-specific messaging and maintenance request workflows.
8. Record relevant property activities in an auditable timeline and notify users about important events.
9. Support handover information, inventory, utility readings, deposit records, and PDF reports.
10. Support offline inspection drafts and reliable synchronization when connectivity returns.
11. Evaluate the correctness, usability, security, and reliability of the main workflows.

### 3.4 Expected benefits

- **Better organization:** inspection evidence is connected to the property, room, and inspection.
- **Clearer communication:** discussions can be found in the relevant property context.
- **Improved follow-up:** maintenance requests and reminders have visible statuses.
- **More transparent review:** each party's response is stored independently.
- **Better handover records:** keys, inventory, meter readings, and deposit discussions are recorded together.
- **Traceability:** important actions and evidence changes can be reviewed later.
- **Convenience:** users can capture evidence using a mobile device and continue offline drafts when necessary.

### 3.5 What success does not mean

ProofNest does not guarantee that parties will agree, that every issue will be detected, that a photograph is genuine, or that a record will be legally accepted in every jurisdiction. It provides organized records and workflows to support communication and review.

---

## 4. Scope

### 4.1 In-scope feature groups

All feature groups below are included in the updated project scope. Implementation will be staged so the core inspection workflow is completed before secondary refinements.

| Feature group | Included capabilities | Initial delivery priority |
|---|---|---|
| A. Landlord–Tenant Chat | Private direct conversations, property-specific discussions, inspection/maintenance context, text messages, photo/document attachments, timestamps, read status, message history, search, notifications. | Must |
| B. Guided Room-by-Room Inspection | Room checklist, item condition, comments, photos, progress, missing-evidence prompts, draft saving. | Must |
| C. Joint Inspection Approval | Separate landlord and tenant comments and accept/dispute responses, unresolved state, acknowledgement history. | Must |
| D. Evidence Protection and Activity History | Original file references, uploader/time metadata, SHA-256 hash, explicit version history, audit events. | Must |
| E. Property Timeline | Chronological property events for inspections, findings, messages, maintenance, handover, and reports. | Should |
| F. Reminders and Notifications | In-app notifications and reminders for messages, inspections, maintenance, findings, responses, and appointments. | Should |
| H. Digital Reports and Acknowledgements | PDF report, evidence references, findings, comments, separate responses, generated timestamp, receipt acknowledgement. | Must |
| I. Deposit and Handover Records | Original deposit amount, proposed deductions, supporting evidence, party responses, keys, access cards, and handover status. | Should |
| J. Offline Inspection Mode | Local drafts, photos, notes, checklist progress, queued uploads, synchronization and conflict handling. | Should; simplified implementation acceptable |
| K. Utility Meter and Item Handover | Electricity/water/gas readings where relevant, meter photos, appliance/furniture/key inventory, move-in/move-out readings. | Should |
| L. Secure Tenant Invitation | Property-specific invitation, acceptance, membership status, revocation, and access history. | Must |

**Feature G — AI Inspection Assistant is intentionally excluded.** The project will not contain AI damage detection, AI image analysis, or automatically generated inspection findings.

### 4.2 Core capabilities retained from the original ProofNest concept

- User registration, authentication, logout, and profile basics.
- Landlord and tenant roles assigned through property membership.
- Property and room management.
- Multiple inspections per property.
- Move-in and move-out inspection types.
- Photo and note capture.
- Manual side-by-side evidence comparison.
- Findings, comments, and party responses.
- Evidence hash calculation and audit history.
- PDF inspection report.

### 4.3 Delivery priorities

- **Must:** required for a credible end-to-end project demonstration.
- **Should:** included in the project scope and implemented after the Must workflows are stable.
- **Could:** useful refinement if time permits; does not replace core testing.

Priority is a delivery order, not a removal from the agreed project direction. If the academic deadline is fixed, simplified versions of secondary features may be delivered first, with limitations documented.

---

## 5. Stakeholders and user roles

### 5.1 Stakeholders

| Stakeholder | Interest / responsibility |
|---|---|
| Landlord / property owner | Creates properties, invites tenants, participates in inspections, reviews findings, manages maintenance, and records handover information. |
| Tenant | Joins an authorized property, participates in inspections, submits evidence, reports maintenance issues, communicates, and responds to findings. |
| Property manager (optional role) | May be granted limited management permissions by a property owner if this role is implemented. |
| Project developer | Designs, implements, tests, documents, and demonstrates the application. |
| Academic supervisor / evaluator | Reviews project value, requirements, software design, implementation, testing, and results. |
| System administrator (technical role) | Maintains the application and operational configuration; not automatically a participant in private property conversations. |

### 5.2 User-role principle

A person's role is determined by their membership in a particular property or tenancy. The same account may be a landlord for one property and a tenant for another. The system must not assume one permanent global role for every property.

### 5.3 Permission model

| Capability | Landlord / owner | Tenant | Property manager, if supported |
|---|---|---|---|
| Create property | Yes | No | Only if delegated |
| Edit property details | Yes | Limited; report correction request if required | Delegated |
| Invite tenant | Yes | No | Only if delegated |
| View shared property details | Yes | Yes, while membership is active | Delegated |
| Create inspection | Yes | Yes, if property rules allow | Delegated |
| Add photos and notes | Yes | Yes | Yes, if authorized |
| Create finding | Yes | Yes | Yes, if authorized |
| Submit own finding response | Yes | Yes | Only their own response if the response workflow permits |
| View other party's response | Yes | Yes | Only if authorized for the property |
| Send property chat messages | Yes | Yes | Only if included in the conversation |
| Submit maintenance request | Yes | Yes | Yes, if authorized |
| Change maintenance status | Yes | May add updates/comment; cannot mark landlord-only completion unless configured | Delegated |
| View handover and shared reports | Yes | Yes | Delegated |
| Revoke tenant membership | Yes | No | Only if owner delegates this permission |

### 5.4 Authorization requirements

1. Every private property record must be checked against the authenticated user's active property membership.
2. Membership status must be checked by the backend for every protected API request.
3. Removing a user from a property must revoke future access to its private records and conversations, subject to any explicitly approved retention/export rule.
4. A conversation must have a property association and an authorized participant list.
5. The mobile interface must not be treated as the security boundary. Hiding a button is not sufficient; the backend must enforce permissions.

---

## 6. User journeys

### 6.1 New landlord journey

1. Landlord installs and opens ProofNest.
2. Landlord creates an account and signs in.
3. Landlord creates a property and enters its basic details.
4. Landlord adds rooms/areas and optional inventory items.
5. Landlord generates a property-specific tenant invitation.
6. Tenant receives and accepts the invitation.
7. The property becomes visible to both authorized participants.
8. Landlord starts a move-in inspection and invites the tenant to participate.

### 6.2 New tenant journey

1. Tenant creates an account or signs in.
2. Tenant opens the invitation link/code supplied by the landlord.
3. Tenant reviews the property details and accepts the invitation.
4. Tenant can access only the property for which the invitation was issued.
5. Tenant joins the inspection, adds photos and notes, and reviews the shared record.
6. Tenant can use property chat and submit maintenance requests while the membership is active.

### 6.3 Move-in inspection journey

1. An authorized participant creates a move-in inspection.
2. The inspection includes date, participants, property, and rooms.
3. The user opens a room checklist.
4. For each checklist item, the user records a condition, note, and relevant photo(s).
5. The application displays progress and missing required items.
6. The user saves the inspection as a draft or submits it for review.
7. The other party reviews the available record and adds their own evidence or comments.
8. Each party's acknowledgement/response is stored separately.
9. The inspection report can be generated and acknowledged.

### 6.4 During-tenancy maintenance journey

1. Tenant or another authorized participant creates a maintenance request.
2. The request identifies the property, room/location if relevant, category, description, urgency, and attachments.
3. Landlord receives a notification and reviews the request.
4. The landlord acknowledges the request and can communicate through its linked conversation.
5. A visit or repair date is recorded if required.
6. The status changes through the agreed repair workflow.
7. Repair photographs, receipts, and completion notes can be added.
8. The requester reviews the outcome and adds a comment or reopens the request if the issue remains unresolved.
9. Events are added to the property timeline.

### 6.5 Chat journey

1. An authorized participant opens a property conversation or a conversation linked to an inspection/maintenance request.
2. The participant sends a text message and optional attachment.
3. The backend verifies that the sender is an active member of the property and a member of the conversation.
4. The message is stored with sender and server timestamp.
5. Other authorized members receive an in-app notification.
6. Read status is updated when the message is opened, where supported.
7. Conversation history remains associated with the property.

### 6.6 Move-out and comparison journey

1. An authorized participant creates a move-out inspection for the same property/tenancy.
2. The participant completes the room-by-room checklist and captures current condition evidence.
3. ProofNest displays move-in and move-out evidence together for manual comparison.
4. A participant creates a finding for an observed difference and links the relevant evidence.
5. The other party reviews the finding, adds comments, and accepts or disputes it.
6. ProofNest preserves both responses and displays whether the finding is mutually acknowledged, disputed, or awaiting a response.
7. A report is generated with the relevant evidence and responses.

### 6.7 End-of-tenancy handover journey

1. The participants open a handover record for the tenancy.
2. They review keys, access cards, appliances, furniture, and other inventory items.
3. Utility meter readings are entered and supported with photographs.
4. The original deposit amount is recorded for reference.
5. If the landlord proposes deductions, each proposed item includes a reason, amount, and supporting evidence where available.
6. The tenant records a separate response to each proposed deduction.
7. The participants record handover acknowledgements and any unresolved points.
8. The final handover/report is generated and the tenancy membership can be closed or archived according to the system rules.

### 6.8 Offline inspection journey

1. An authorized user opens an inspection that is available on the device.
2. If connectivity is unavailable, the user continues with supported checklist, note, and photo-capture actions.
3. Changes are stored locally and marked as pending synchronization.
4. When connectivity returns, the app sends queued changes and file uploads to the backend.
5. The server validates permissions and record versions before accepting the changes.
6. Successful items are marked synchronized; failures remain visible with a retry option.
7. Conflicting edits are not silently overwritten. The app requests user review or applies the documented field-level conflict rule.

---

## 7. End-to-end business process

This section describes the intended process from property setup to the end of a tenancy.

### Stage 1 — Account creation and authentication

1. The user opens ProofNest and chooses registration or login.
2. Registration collects the required profile information and password.
3. The backend validates the input, normalizes the email address, and checks for duplicates.
4. The password is securely hashed before storage.
5. The user signs in and receives an authenticated session/token.
6. The application loads the user's authorized properties and notifications.

**Output:** active user account and authenticated session.

### Stage 2 — Property creation

1. Landlord creates a property.
2. Landlord enters a property name/reference, address, property type, and optional description.
3. Landlord adds rooms/areas, such as living room, kitchen, bedroom, bathroom, and balcony.
4. Landlord may add inventory items such as appliances, furniture, keys, and access cards.
5. The backend creates the property owner membership.

**Output:** property record with rooms/areas and an owner membership.

### Stage 3 — Tenant invitation and access

1. Landlord creates an invitation for a specific property and, where applicable, tenancy.
2. The invitation contains a secure, time-limited token or code.
3. Tenant opens the invitation and authenticates or creates an account.
4. The backend validates expiry, status, intended property, and any configured email restriction.
5. Tenant accepts the invitation.
6. The backend creates an active tenant membership and records the event.

**Output:** tenant has access only to the invited property.

### Stage 4 — Move-in inspection

1. Landlord or authorized participant starts a move-in inspection.
2. The app loads the property rooms and inspection checklist.
3. Participants inspect each room and record item conditions.
4. Participants capture photographs and notes.
5. The app saves progress and identifies missing required items.
6. Evidence is uploaded to private storage and linked to the relevant inspection, room, and checklist item.
7. The backend stores upload metadata and a SHA-256 hash for each file.
8. The inspection is submitted for review and the other party is notified.
9. Each party records comments and an acknowledgement/response.
10. A report is generated if requested.

**Output:** move-in inspection record, evidence set, responses, and optional report.

### Stage 5 — Tenancy communication and maintenance

1. Users communicate in a property-specific conversation.
2. A user reports a maintenance problem with category, description, urgency, and optional photos.
3. The other party is notified.
4. The request is acknowledged, scheduled, worked on, and marked resolved or reopened.
5. Related discussion and attachments remain linked to the request/property.
6. The property timeline records relevant events.

**Output:** traceable communication and maintenance history.

### Stage 6 — Move-out inspection

1. An authorized participant starts a move-out inspection for the relevant property/tenancy.
2. The room/checklist structure is reused where possible to support consistent comparison.
3. Participants record current conditions, photographs, and notes.
4. The inspection is saved/submitted and the other party is notified.

**Output:** move-out inspection evidence.

### Stage 7 — Manual comparison and findings

1. A participant selects the move-in and move-out inspections to compare.
2. The app groups evidence by room and checklist item where possible.
3. The user views selected photos side by side.
4. The user creates a finding describing the observed difference and links supporting evidence.
5. The finding is assigned a category/severity if those fields are configured.
6. The other party receives a notification and reviews the finding.

**Output:** comparison session and findings linked to evidence.

### Stage 8 — Separate responses and dispute handling

1. Each party reviews a finding.
2. Each party may comment and select an independent response: accept, dispute, or request clarification (if implemented).
3. The application stores each response with the responding user and timestamp.
4. If both parties accept, the finding is shown as mutually acknowledged.
5. If one party disputes or responses conflict, the finding remains disputed/unresolved.
6. No party's response may overwrite the other party's response.
7. The system does not automatically determine fault or legal responsibility.

**Output:** finding with separate participant positions and an accurate status.

### Stage 9 — Handover, inventory, utilities, and deposit record

1. Participants open or create the handover record.
2. They confirm keys, access cards, appliances, furniture, and other inventory.
3. They enter final meter readings and attach meter photographs.
4. The original deposit amount is recorded.
5. Landlord may create proposed deduction entries with reasons, amounts, and supporting documents/photos.
6. Tenant may accept or dispute each proposed deduction and add comments/evidence.
7. The application records acknowledgements and unresolved items.

**Output:** handover record and deposit discussion record; no money is transferred by ProofNest.

### Stage 10 — Final report and archive

1. An authorized participant generates a final report.
2. The report includes selected inspection information, evidence, findings, separate responses, maintenance/handover information where selected, and generation time.
3. Each party may acknowledge receipt of the report separately.
4. The tenancy/property membership may be closed or archived by an authorized user.
5. Records remain subject to the retention and access policy.

**Output:** downloadable report, acknowledgement history, and archived tenancy record.

---

## 8. Functional requirements

Functional requirements use the format **FR-XXX**. The Must/Should priority indicates implementation order for the academic prototype.

### 8.1 Authentication and user profile

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-001 | The application shall allow a user to register with required profile information and a unique email address. | Must | Valid registration creates one account; duplicate email is rejected with a clear message. |
| FR-002 | The application shall allow a registered user to sign in and sign out. | Must | Correct credentials create an authenticated session; invalid credentials are rejected. |
| FR-003 | The application shall securely store passwords using an appropriate one-way password-hashing mechanism. | Must | Plain-text passwords are never stored or returned by the API. |
| FR-004 | The application shall allow users to view and update basic profile information. | Should | Updates are validated and saved to the correct account. |
| FR-005 | The application shall support a user being a landlord on one property and a tenant on another. | Must | Role and permissions are resolved from property membership, not a single global role. |
| FR-006 | The application shall provide a secure authenticated session/token mechanism for the Flutter client. | Must | Protected API requests require valid authentication; expired/revoked sessions are rejected. |

### 8.2 Property management and secure invitations

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-010 | An authorized landlord shall be able to create a property. | Must | Property is saved with an owner membership. |
| FR-011 | An authorized user shall be able to view and update permitted property information. | Must | Changes are validated and recorded. |
| FR-012 | A property shall support multiple rooms/areas. | Must | Rooms can be added, edited, listed, and associated with inspections. |
| FR-013 | A landlord shall be able to create a property-specific tenant invitation. | Must | Invitation is unique, linked to one property, and has an expiry/status. |
| FR-014 | A tenant shall be able to accept a valid invitation. | Must | Acceptance creates an active membership only for the invited property. |
| FR-015 | An authorized landlord shall be able to revoke an invitation or end a membership. | Must | Revocation is recorded and future access is blocked. |
| FR-016 | The application shall display the user's authorized properties. | Must | Only properties with valid membership are listed. |
| FR-017 | The system shall support property-specific inventory records. | Should | Inventory items can be listed and associated with a property or room. |

### 8.3 Inspection creation and lifecycle

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-020 | An authorized participant shall be able to create an inspection for a property. | Must | Inspection is linked to exactly one property and records its creator. |
| FR-021 | The application shall support at least Move-in and Move-out inspection types. | Must | Type is stored and displayed on the inspection. |
| FR-022 | An inspection shall have a status such as Draft, In Progress, Submitted, Under Review, Completed, or Archived. | Must | Allowed status transitions are validated by the backend. |
| FR-023 | An inspection shall record its date, creator, participants, and relevant tenancy/property. | Must | Required metadata is present and retrievable. |
| FR-024 | Participants shall be able to save an incomplete inspection as a draft. | Must | Draft progress is retained and can be resumed by an authorized user. |
| FR-025 | The application shall display inspection progress. | Should | Completed and outstanding rooms/items are visible. |
| FR-026 | The application shall support a consistent room/item structure between move-in and move-out inspections. | Must | The user can identify corresponding rooms/items during comparison. |

### 8.4 Guided room-by-room checklist

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-030 | The application shall provide a checklist for each room/area. | Must | Each inspection can list the relevant rooms and checklist items. |
| FR-031 | A participant shall be able to record a condition for a checklist item. | Must | Condition and optional notes are saved against the correct inspection item. |
| FR-032 | A participant shall be able to add notes to a room or checklist item. | Must | Note author and timestamp are stored. |
| FR-033 | The application shall allow photographs to be attached to a room/checklist item. | Must | Photo is linked to the correct inspection and item. |
| FR-034 | The application shall show incomplete checklist items and missing required evidence. | Should | Missing items are visible before submission. |
| FR-035 | The application shall allow authorized participants to add evidence during the inspection review stage. | Must | Additional evidence is linked and its upload event is recorded. |

### 8.5 Photo evidence and evidence integrity

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-040 | The application shall allow a user to capture or select photographs from the device. | Must | Supported image types can be selected/captured. |
| FR-041 | The application shall validate permitted file types and configured file-size limits. | Must | Unsupported or oversized files are rejected with a clear message. |
| FR-042 | The system shall store evidence files in private storage. | Must | Files are not exposed through unrestricted public URLs. |
| FR-043 | The system shall store evidence metadata including uploader, upload time, property, inspection, room/item, and file reference. | Must | Metadata can be retrieved and displayed to authorized users. |
| FR-044 | The system shall calculate a SHA-256 hash for each uploaded original file. | Must | Hash is stored and can be recalculated for an integrity check. |
| FR-045 | The system shall retain an original evidence record when a revised/replacement file is uploaded. | Must | The new file is recorded as a new version or linked replacement; original history is not silently overwritten. |
| FR-046 | The application shall show evidence details and upload status. | Should | Users can distinguish pending, uploaded, failed, and verified files. |
| FR-047 | The system shall support image preview for authorized users. | Must | Private file access is checked before a preview is served. |

### 8.6 Manual comparison, findings, and responses

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-050 | The application shall allow authorized users to select move-in and move-out inspections for the same property/tenancy. | Must | Comparisons cannot join unrelated properties. |
| FR-051 | The application shall display selected move-in and move-out photographs side by side. | Must | Both images and their metadata are visible in a comparison view. |
| FR-052 | The application shall group comparison evidence by room or checklist item where possible. | Must | Corresponding items can be navigated. |
| FR-053 | A participant shall be able to create a finding describing a possible difference. | Must | Finding is linked to the property and relevant inspection/evidence. |
| FR-054 | A finding shall support a category, description, and optional severity/priority. | Should | Values are validated and visible to authorized participants. |
| FR-055 | A participant shall be able to add comments to a finding. | Must | Comment includes author, time, and finding reference. |
| FR-056 | Landlord and tenant shall be able to submit separate accept/dispute responses. | Must | Each response is stored independently and neither overwrites the other. |
| FR-057 | The application shall show whether a finding is awaiting response, mutually acknowledged, disputed, or unresolved. | Must | Status reflects both parties' recorded responses. |
| FR-058 | The system shall preserve response and finding-status history. | Must | Previous response/status events remain reviewable. |
| FR-059 | The system shall not assign legal responsibility or decide liability automatically. | Must | UI and reports describe observations and party positions, not legal conclusions. |

### 8.7 Landlord–tenant chat

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-060 | An authorized participant shall be able to open a conversation associated with a property. | Must | Conversation is linked to one property and its authorized members. |
| FR-061 | The application shall support direct text messaging between authorized participants. | Must | Message content, sender, conversation, and server timestamp are stored. |
| FR-062 | A conversation may be linked to an inspection, finding, or maintenance request. | Should | The conversation displays its related record context. |
| FR-063 | Users shall be able to attach supported photos and documents to messages. | Should | Attachment is validated, privately stored, and linked to the message. |
| FR-064 | The application shall display message history in chronological order. | Must | Authorized participants can load earlier messages. |
| FR-065 | The application shall support message delivery/read status where feasible. | Should | Status is clearly labelled and does not falsely imply that a message was understood or agreed. |
| FR-066 | The application shall notify participants about new messages. | Should | A notification is created for eligible recipients. |
| FR-067 | The application shall support searching messages within an authorized conversation. | Could | Search results are limited to the user's permitted conversations. |
| FR-068 | The backend shall verify conversation membership and property membership before returning or accepting messages. | Must | Unrelated users cannot read or send messages to the conversation. |

### 8.8 Maintenance requests

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-070 | An authorized tenant or landlord shall be able to create a maintenance request. | Must | Request is linked to a property and creator. |
| FR-071 | A request shall support category, description, urgency, location/room, attachments, creation time, and status. | Must | Required information is validated and saved. |
| FR-072 | The system shall support statuses such as Submitted, Acknowledged, Scheduled, In Progress, Resolved, and Reopened. | Must | Status changes follow allowed transitions and create history events. |
| FR-073 | The landlord or authorized manager shall be able to acknowledge and update a request. | Must | Only authorized users can perform restricted status changes. |
| FR-074 | Users shall be able to discuss a maintenance request through linked chat or comments. | Should | The discussion remains connected to the request. |
| FR-075 | Users shall be able to add repair completion notes and supporting photographs/documents. | Should | Completion evidence is linked to the request. |
| FR-076 | The requester shall be able to comment on the resolution or request reopening. | Should | Reopening/comment is recorded and the request status updates according to rules. |
| FR-077 | Maintenance events shall appear in the property timeline. | Should | A request and its status history are visible chronologically. |

### 8.9 Property timeline and activity history

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-080 | The application shall show a chronological timeline for each property. | Should | Authorized users can view relevant events in date order. |
| FR-081 | Timeline events shall include relevant inspections, evidence uploads, findings, responses, messages, maintenance, handover, and report activities. | Should | Event type, actor where appropriate, timestamp, and linked record are available. |
| FR-082 | The application shall allow a user to open the related record from a timeline event. | Could | Navigation opens the correct authorized record. |
| FR-083 | Important events shall be written to an audit log that is not editable through ordinary user workflows. | Must | Key event history can be tested and reviewed. |

### 8.10 Notifications and reminders

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-090 | The application shall create in-app notifications for selected events. | Should | Eligible recipients see the correct notification. |
| FR-091 | Notifications shall cover new messages, invitations, inspection requests, findings, responses, maintenance updates, and report acknowledgements. | Should | Each configured event creates the intended notification. |
| FR-092 | Users shall be able to mark a notification as read. | Should | Read status is saved for the correct user. |
| FR-093 | The application shall support reminders for upcoming inspections and scheduled maintenance. | Should | Reminder is shown at the configured time. |
| FR-094 | Users shall be able to control supported notification preferences. | Could | Preference changes affect future notifications. |

### 8.11 Reports and acknowledgements

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-100 | An authorized user shall be able to generate a PDF inspection report. | Must | A valid PDF is generated for the selected inspection/comparison. |
| FR-101 | The report shall include property/inspection details, selected evidence, findings, and party responses. | Must | Report contents match stored records at generation time. |
| FR-102 | The report shall include its generation timestamp and report identifier. | Should | Generated report can be identified and retrieved. |
| FR-103 | A user shall be able to acknowledge receipt of a report separately from accepting its findings. | Should | Receipt acknowledgement does not change finding responses. |
| FR-104 | The application shall preserve the report version or a snapshot of the records used when the report was generated. | Should | Later record changes do not silently alter an already generated report. |
| FR-105 | Authorized users shall be able to download or share a report using protected access. | Must | Report access is checked by the backend. |

### 8.12 Deposit and handover records

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-110 | An authorized user shall be able to create a handover record for a tenancy. | Should | Handover is linked to the property and tenancy. |
| FR-111 | The handover record shall contain key, access-card, appliance, furniture, and other inventory checklist items. | Should | Items can be marked handed over, returned, missing, or needing review. |
| FR-112 | The system shall record the original deposit amount and currency for reference. | Should | Amount and currency are stored without initiating a payment. |
| FR-113 | An authorized landlord shall be able to record a proposed deposit deduction with an amount, reason, and supporting evidence reference. | Should | Each proposed deduction is individually identifiable. |
| FR-114 | The tenant shall be able to respond to each proposed deduction. | Should | Tenant response is independent and timestamped. |
| FR-115 | The application shall display proposed, accepted, disputed, and unresolved deduction states without deciding legal entitlement. | Should | Status follows recorded participant responses. |
| FR-116 | Both parties shall be able to acknowledge handover information separately. | Should | Each acknowledgement is stored with user and timestamp. |

### 8.13 Utility readings and inventory

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-120 | The application shall record electricity and water meter readings and optionally gas readings. | Should | Reading value, unit/type, date, recorder, and property are stored. |
| FR-121 | A user shall be able to attach a photograph to a meter reading. | Should | Photo is stored as private evidence linked to the reading. |
| FR-122 | The application shall show initial and final readings side by side. | Should | The user can review the recorded values and dates. |
| FR-123 | The application shall support inventory items for furniture, appliances, keys, and access cards. | Should | Each item has a name/category and condition/status fields. |
| FR-124 | Inventory condition may be recorded during move-in and move-out. | Should | The application can display the two recorded conditions for review. |

### 8.14 Offline inspection and synchronization

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-130 | The application shall allow an authorized user to save an inspection draft locally when offline. | Should | Draft notes/checklist changes remain available after the app is closed and reopened. |
| FR-131 | The application shall allow supported photographs to be captured/queued locally while offline. | Should | Pending files are retained until synchronization or user removal. |
| FR-132 | The application shall display pending, synchronizing, synchronized, and failed states. | Should | Users can identify the state of each queued change/file. |
| FR-133 | The application shall retry failed synchronization without duplicating records. | Should | Retry uses stable client-generated identifiers/idempotency keys. |
| FR-134 | The application shall detect conflicting updates where local and server records have changed. | Should | Conflicts are not silently overwritten; a defined resolution process is shown. |
| FR-135 | The backend shall validate authorization when queued changes are synchronized. | Must | Revoked users cannot upload or change protected records after access is revoked. |

### 8.15 System administration and support controls

| ID | Requirement | Priority | Acceptance criteria |
|---|---|---|---|
| FR-140 | The application shall provide clear validation, upload, network, and permission error messages. | Must | Users receive understandable feedback and can recover where possible. |
| FR-141 | The application shall record operational events required for debugging without logging passwords or sensitive message content unnecessarily. | Must | Logs support troubleshooting without exposing credentials. |
| FR-142 | The application shall support a controlled method for deactivating or archiving a tenancy/property membership. | Should | Access changes are recorded and historical records remain governed by retention rules. |

---

## 9. Business rules

1. Every property must have an owner or an explicitly authorized manager.
2. A user may have different roles on different properties.
3. Every tenant invitation must refer to one property and have a unique token/code, expiry, and status.
4. Only an authenticated user with active property membership may access private property records.
5. Every inspection belongs to one property and, where tenancy records are implemented, one relevant tenancy.
6. Move-in and move-out inspections for comparison must belong to the same property and relevant tenancy.
7. Evidence must be linked to an inspection and, where applicable, a room/checklist item.
8. Original uploaded evidence must not be silently overwritten.
9. The original evidence file reference, upload metadata, and calculated SHA-256 hash should be retained.
10. A hash mismatch indicates that file bytes differ from the bytes used to calculate the stored hash; it does not prove who changed the file or when the original photograph was taken.
11. A finding may link to multiple evidence records from one or more inspections.
12. Landlord and tenant responses must be stored independently.
13. A finding is mutually acknowledged only when the required parties have recorded acceptance under the configured response rules.
14. If a required party disputes a finding or the responses conflict, the finding remains disputed/unresolved.
15. A report receipt acknowledgement is not the same as accepting the findings or agreeing to a deposit deduction.
16. ProofNest does not determine legal fault, liability, or entitlement to a deposit.
17. Messages are private to authorized conversation participants and the relevant property membership.
18. A user cannot add themselves to a conversation merely by knowing its identifier.
19. Maintenance status changes must be recorded with actor, time, and previous/new status.
20. A resolved maintenance request may be reopened through an explicit action; its previous resolution history is retained.
21. Handover and inventory statuses must describe what was recorded, not make an unsupported legal conclusion.
22. Utility readings must retain their unit/type and recorded date so that values are not compared incorrectly.
23. Deposit records are informational records only; no deposit payment, refund, or bank transfer is processed by the application.
24. Offline changes must be associated with the authenticated user and validated again by the backend during synchronization.
25. A failed synchronization must remain visible to the user and must not be presented as a successfully saved server record.
26. Timestamps should be stored in a consistent server-side format (preferably UTC) and displayed in the user's local time zone.
27. Deactivated memberships must not grant new access, but retained historical records must follow the documented retention and privacy policy.
28. No AI inspection assistant or AI-generated inspection decision is included in this project.

---

## 10. Mobile application screens and navigation

### 10.1 Main navigation

The proposed bottom navigation contains five primary destinations:

1. **Home:** dashboard, alerts, upcoming actions, recent property activity.
2. **Properties:** property list, property details, rooms, members, inventory, maintenance, handover, and utility readings.
3. **Inspections:** move-in/move-out records, guided checklists, photos, comparisons, findings, and approvals.
4. **Messages:** property conversations, inspection discussions, maintenance discussions, and attachments.
5. **Profile:** personal details, security, notification preferences, and account settings.

Reports and notifications may be accessed from Home, Property Details, Inspection Details, or Profile rather than requiring separate bottom-navigation slots.

### 10.2 Screen inventory

| Screen | Main purpose | Primary actions |
|---|---|---|
| Splash / onboarding | Introduce the product and load the session | Continue, sign in |
| Login | Authenticate an existing user | Sign in, open registration |
| Registration | Create an account | Register, select initial role if required |
| Home dashboard | Summarize relevant activity | Open property, inspection, message, notification |
| Property list | Show authorized properties | Search, open, create property (permission-dependent) |
| Add/edit property | Capture property information | Save, cancel |
| Property details | Show property, rooms, participants, and activity | Invite tenant, open room, timeline, maintenance, handover |
| Room management | Organize rooms/areas | Add, edit, archive room |
| Tenant invitation | Grant property-specific access | Generate, share, accept, revoke |
| Inspection list | Show inspection records | Filter, create, resume |
| Create inspection | Set inspection type/date/participants | Create move-in or move-out inspection |
| Room checklist | Record item conditions | Select condition, add note/photo, save |
| Evidence capture/gallery | Capture or select files | Take photo, select image, preview, upload |
| Inspection details | Display inspection progress and evidence | Submit, add evidence, open report |
| Comparison | View selected move-in/move-out evidence | Select room/item, compare, create finding |
| Finding details | Review observation and responses | Comment, accept, dispute, attach evidence |
| Messages list | Show conversations | Open or start authorized conversation |
| Chat conversation | Exchange messages and attachments | Send, search, attach, view related record |
| Maintenance list | Track requests | Create, filter, open request |
| Maintenance details | Follow a repair | Update status, schedule, discuss, attach completion proof |
| Notifications | Show alerts and reminders | Open item, mark read |
| Property timeline | Show chronological events | Filter and open related record |
| Handover | Record end-of-tenancy handover | Complete checklist, acknowledge |
| Deposit record | Show original deposit and proposed deductions | Add proposal, respond, attach evidence |
| Utility readings | Record meter values | Add reading/photo, compare readings |
| Inventory | Track furniture, appliances, keys, and cards | Add item, update condition, confirm handover |
| Reports | Generate and view reports | Preview, generate, download/share securely |
| Profile/settings | Manage user preferences | Edit profile, security, notification preferences, sign out |

### 10.3 UI/UX design direction

- Primary background: black `#0B0B0B` or near-black `#06090D`.
- Primary accent: crimson `#8B0D1A`; brighter interactive red may be used for emphasis.
- Main text: off-white `#F5F2ED`.
- Cards: charcoal/dark navy surfaces.
- Borders: subtle blue-grey or neutral grey.
- Status indicators: consistent labels and icons, not colour alone.
- Components: compact cards, clear typography, visible buttons, readable forms, and mobile-friendly touch targets.
- Brand tagline: **Every Detail. Every Proof. One Place.**

The mobile interface must be designed for phone-sized screens first. Desktop-style sidebars from the earlier web concepts should not be copied directly into the mobile layout; use bottom navigation, mobile app bars, and stacked cards.

---

## 11. Data requirements and conceptual data model

The database design should be normalized and should preserve relationships between property, tenancy, inspection, evidence, finding, conversation, maintenance, and handover records. The following is a conceptual entity list; final field types, constraints, and foreign keys will be defined in the ERD and implementation.

### 11.1 Identity and access entities

| Entity | Purpose / important information |
|---|---|
| `users` | User ID, full name, email, password hash, account status, profile data, created/updated timestamps. |
| `properties` | Property ID, owner ID, name/reference, address, type, description, status, timestamps. |
| `property_memberships` | Property ID, user ID, membership role, permission set, status, invited/accepted/ended dates. |
| `tenant_invitations` | Property ID, inviter, token hash/code, expiry, invite status, intended email if used, accepted user/time. |
| `tenancies` | Property ID, landlord/tenant association, start/end dates, status, optional non-sensitive tenancy reference. Used to distinguish successive occupants of the same property. |

### 11.2 Property structure and inspection entities

| Entity | Purpose / important information |
|---|---|
| `rooms` | Property ID, room name/type, description, display order, active status. |
| `checklist_templates` | Reusable checklist template or version for a room/property type. |
| `checklist_template_items` | Template item name, category, required flag, sort order. |
| `inspections` | Property ID, tenancy ID if applicable, type (MOVE_IN/MOVE_OUT), date, creator, status, submitted/completed timestamps. |
| `inspection_participants` | Inspection ID, user ID, participant role, invitation/participation status, acknowledgement fields if appropriate. |
| `inspection_room_items` | Inspection ID, room ID, checklist item/reference, condition, notes, completion state. |
| `comparison_sessions` | Property/tenancy ID, move-in inspection ID, move-out inspection ID, creator, creation time, status. |

### 11.3 Evidence, findings, and audit entities

| Entity | Purpose / important information |
|---|---|
| `evidence` | Evidence ID, property/inspection/room/item references, uploader, private storage key, original filename, MIME type, size, SHA-256 hash, captured/uploaded timestamps, status. |
| `evidence_versions` | Evidence ID, version number, storage key, hash, uploader, timestamp, reason/change description; retains previous versions. |
| `findings` | Property/inspection/comparison references, room/item, title/description, category, severity, status, creator, timestamps. |
| `finding_evidence` | Many-to-many link between findings and evidence records. |
| `finding_comments` | Finding ID, author, comment, created timestamp. |
| `finding_responses` | Finding ID, responding user, participant role, response type (ACCEPT/DISPUTE/CLARIFICATION), explanation, timestamp. |
| `audit_events` | Actor, property, event type, target entity/ID, timestamp, safe metadata, correlation/request ID where useful. |

### 11.4 Chat and maintenance entities

| Entity | Purpose / important information |
|---|---|
| `conversations` | Property ID, optional linked inspection/finding/maintenance request, conversation type, created/closed status, timestamps. |
| `conversation_members` | Conversation ID, user ID, membership status, joined/left dates, last-read marker. |
| `messages` | Conversation ID, sender ID, message text/type, server timestamp, edit/delete state if supported, client-generated message ID. |
| `message_attachments` | Message ID, private file key, MIME type, size, upload metadata, hash. |
| `maintenance_requests` | Property ID, tenancy/room if relevant, creator, category, description, urgency, status, assigned responsible participant, created/updated/resolved dates. |
| `maintenance_updates` | Request ID, actor, previous/new status, comment, scheduled date, timestamp. |
| `maintenance_attachments` | Request/update ID and private evidence-file reference. |

### 11.5 Notifications, timeline, and reports

| Entity | Purpose / important information |
|---|---|
| `notifications` | Recipient, event type, title/body/reference, created/read timestamps, delivery state. |
| `timeline_events` | Property ID, event type, linked entity, actor, event timestamp, display-safe summary. May be generated from audit events rather than duplicated if the design allows. |
| `reports` | Property/inspection/comparison/handover references, report type, file key, generation time, generated-by user, version/snapshot reference. |
| `report_acknowledgements` | Report ID, user ID, acknowledgement type (for example RECEIVED), comment, timestamp. |

### 11.6 Handover, deposit, utility, and inventory entities

| Entity | Purpose / important information |
|---|---|
| `handover_records` | Property/tenancy ID, inspection references, created-by user, handover date, status, notes. |
| `handover_responses` | Handover ID, user ID, acknowledgement/response, comment, timestamp. |
| `deposit_records` | Tenancy ID, original deposit amount, currency, recorded-by user, recorded date, notes. |
| `deposit_deductions` | Deposit record ID, description/reason, proposed amount, evidence reference, proposer, status. |
| `deposit_responses` | Deduction ID, responding user, accept/dispute response, explanation, timestamp. |
| `utility_readings` | Property/tenancy ID, utility type, value, unit, meter reference, reading date, recorder, photo evidence reference. |
| `inventory_items` | Property/room ID, item name/category, identifier/description, expected quantity, initial condition, active status. |
| `inventory_observations` | Inventory item ID, inspection/handover ID, quantity, condition, notes, user, timestamp, evidence reference. |
| `handover_item_records` | Handover ID, item ID, handover state, quantity, condition, notes, evidence, timestamp. |

### 11.7 Offline synchronization entities

| Entity / local structure | Purpose / important information |
|---|---|
| Local inspection drafts | Device-local inspection data, checklist progress, notes, and local media references. |
| `sync_queue` (local) | Client operation ID, entity type, local record ID, operation payload/reference, retry count, last error, status, created time. |
| Server sync/idempotency records | Idempotency key or client operation ID, authenticated user, accepted result, timestamp; prevents duplicate creation on retry. |

### 11.8 Key relationships

- One user can have many property memberships.
- One property can have many memberships, rooms, tenancies, inspections, conversations, maintenance requests, handover records, and timeline events.
- One tenancy can have move-in and move-out inspections and associated deposit/handover records.
- One inspection can have many room checklist records and evidence files.
- One evidence record can be linked to multiple findings through `finding_evidence`.
- One finding can have many comments and separate responses from different parties.
- One conversation has authorized members and many messages; each message may have attachments.
- One maintenance request can have many status updates, attachments, and linked discussion messages.
- One report can have multiple acknowledgements.
- One deposit record can have multiple proposed deductions; each deduction can have multiple responses/evidence references if required.
- One inventory item can have multiple observations across inspections and handovers.

### 11.9 Data-model design notes

1. Use foreign keys for related records and indexes for common property, tenancy, inspection, conversation, and date queries.
2. Use stable unique identifiers (UUIDs or another consistent ID strategy) for records that may be created offline.
3. Use soft deletion/archive states for records that must remain in a traceable history; do not silently remove evidence or audit records through ordinary user actions.
4. Keep file bytes outside MySQL where practical; store private storage keys and metadata in the database.
5. Store money with a decimal type and a currency code; do not use floating-point types for deposit amounts.
6. Store meter values with explicit utility type and unit.
7. Preserve timestamps consistently and convert to local display time in the application.
8. Consider successive tenancies for the same property so a new tenant does not automatically receive access to a previous tenant's private conversations or personal information.

---

## 12. Solution architecture and technology

### 12.1 Recommended architecture

ProofNest will use a mobile-client and backend-service architecture:

```text
+--------------------------------------------------+
|                Flutter Mobile App                |
|                                                  |
| Screens / Widgets / Navigation                  |
| ViewModels / State Management                    |
| Repositories / Local Cache / Sync Queue          |
| Camera / Gallery / File Upload                   |
+--------------------------+-----------------------+
                           |
                      HTTPS / JSON
                           |
+--------------------------v-----------------------+
|             Java Spring Boot Backend             |
|                                                  |
| REST Controllers                                 |
| Authentication and Authorization                 |
| Business Services                                |
| Validation / File Handling / Report Generation   |
| Notification / Chat / Synchronization Services  |
+--------------------------+-----------------------+
                           |
              +------------+------------+
              |                         |
+-------------v-------------+  +--------v-----------+
|       MySQL Database      |  | Private File Store |
| Users / Properties        |  | Photos / Documents |
| Inspections / Messages    |  | Reports / Attachments|
| Maintenance / Handover    |  | Protected storage  |
| Audit / Notifications     |  |                   |
+---------------------------+  +--------------------+
```

### 12.2 Frontend — Flutter

Flutter will provide the mobile user interface and device capabilities.

Responsibilities:
- Render the ProofNest screens and navigation.
- Manage form state and input validation.
- Access the device camera and photo gallery with user permission.
- Preview and queue evidence uploads.
- Maintain supported offline drafts.
- Display chat, maintenance, notification, and report data.
- Communicate with backend REST APIs over HTTPS.

Recommended code organization:

```text
lib/
  app/
    app.dart
    router.dart
    theme/
  core/
    api/
    auth/
    storage/
    sync/
    errors/
    widgets/
  features/
    authentication/
    dashboard/
    properties/
    invitations/
    inspections/
    evidence/
    comparisons/
    findings/
    chat/
    maintenance/
    notifications/
    timeline/
    handover/
    deposit/
    utilities/
    inventory/
    reports/
    profile/
  main.dart
assets/
  images/
  icons/
```

State-management and routing packages should be selected once and used consistently. Avoid mixing several competing approaches.

### 12.3 Current local prototype and future shared services

The current implementation uses Flutter/Dart only and has no backend or database. Device-local registration and sign-in exist only to exercise the app flow; they are not production authentication and do not provide cross-device accounts.

Firebase Authentication and a Firebase data store are candidates for a future phase, but the choice and data model are pending. Before real tenancy data is used, select and configure shared authentication, database rules, private file storage, and authorization. A custom Java service is not part of the current implementation.

### 12.4 Data store (future selection pending)

No shared data store is connected. The future solution must support structured property relationships, record-level access control, offline behavior, and private evidence metadata. Firebase is under consideration; no Firebase project or schema is configured yet.

### 12.5 File storage

Evidence upload and shared file storage are not implemented. The future solution must use private file access and must not expose permanent public file URLs.

### 12.6 Chat delivery approach

Chat is not implemented. Select an authenticated shared messaging approach when choosing the future backend; persistence, authorization, ordering, and history are more important than real-time animation or typing indicators.

### 12.7 Offline data approach

Offline inspection persistence and synchronization are not implemented. The future design must identify which changes are local-only and which have synchronized to the authoritative shared record.

### 12.8 Proposed technology stack

| Layer | Technology | Role |
|---|---|---|
| Mobile frontend | Flutter / Dart | Android-first application |
| Backend | None in the current prototype | Future shared service undecided |
| Authentication | Device-local prototype only; Firebase Authentication is a candidate | Development flow only |
| Database | Not connected; Firebase is a candidate | Future shared application data |
| Data access | Local-only placeholder provider | Empty dashboard and feature lists |
| API format | None in the current prototype | Reassess after Firebase/backend selection |
| File storage | Not implemented | Future private evidence and document storage |
| Reports | Not implemented | Future report generation |
| Messaging | Not implemented | Future chat delivery |
| Version control | Git | Source history and collaboration readiness |
| Build | Flutter tooling | Build and execution |
| Testing | Flutter tests | Automated verification |

---

## 13. Deferred service contract (not implemented)

The following REST endpoints are a legacy proposal, not part of the current Dart-only app. Revisit this contract if a custom service is selected; if Firebase is selected, map these business operations to Firebase services and security rules instead. All protected records still require record-level authorization.

### 13.1 Authentication and profile

```text
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/logout
GET    /api/users/me
PATCH  /api/users/me
```

### 13.2 Properties, rooms, and invitations

```text
GET    /api/properties
POST   /api/properties
GET    /api/properties/{propertyId}
PATCH  /api/properties/{propertyId}
GET    /api/properties/{propertyId}/rooms
POST   /api/properties/{propertyId}/rooms
PATCH  /api/rooms/{roomId}
POST   /api/properties/{propertyId}/invitations
GET    /api/properties/{propertyId}/invitations
POST   /api/invitations/{token}/accept
POST   /api/invitations/{invitationId}/revoke
GET    /api/properties/{propertyId}/members
POST   /api/properties/{propertyId}/members/{userId}/revoke
```

### 13.3 Tenancies and inspections

```text
GET    /api/properties/{propertyId}/tenancies
POST   /api/properties/{propertyId}/tenancies
GET    /api/tenancies/{tenancyId}
PATCH  /api/tenancies/{tenancyId}
GET    /api/properties/{propertyId}/inspections
POST   /api/properties/{propertyId}/inspections
GET    /api/inspections/{inspectionId}
PATCH  /api/inspections/{inspectionId}
POST   /api/inspections/{inspectionId}/submit
POST   /api/inspections/{inspectionId}/participants
GET    /api/inspections/{inspectionId}/checklist
PATCH  /api/inspection-items/{inspectionItemId}
```

### 13.4 Evidence and comparison

```text
POST   /api/inspections/{inspectionId}/evidence
GET    /api/inspections/{inspectionId}/evidence
GET    /api/evidence/{evidenceId}
GET    /api/evidence/{evidenceId}/file
POST   /api/evidence/{evidenceId}/versions
GET    /api/evidence/{evidenceId}/versions
POST   /api/evidence/{evidenceId}/verify
POST   /api/comparisons
GET    /api/comparisons/{comparisonId}
```

### 13.5 Findings and responses

```text
GET    /api/comparisons/{comparisonId}/findings
POST   /api/comparisons/{comparisonId}/findings
GET    /api/findings/{findingId}
PATCH  /api/findings/{findingId}
POST   /api/findings/{findingId}/comments
GET    /api/findings/{findingId}/comments
POST   /api/findings/{findingId}/responses
GET    /api/findings/{findingId}/responses
```

### 13.6 Chat

```text
GET    /api/conversations
POST   /api/conversations
GET    /api/conversations/{conversationId}
GET    /api/conversations/{conversationId}/messages
POST   /api/conversations/{conversationId}/messages
POST   /api/conversations/{conversationId}/read
GET    /api/conversations/{conversationId}/search?q={query}
POST   /api/messages/{messageId}/attachments
```

### 13.7 Maintenance

```text
GET    /api/properties/{propertyId}/maintenance
POST   /api/properties/{propertyId}/maintenance
GET    /api/maintenance/{requestId}
PATCH  /api/maintenance/{requestId}
POST   /api/maintenance/{requestId}/status
POST   /api/maintenance/{requestId}/updates
POST   /api/maintenance/{requestId}/attachments
```

### 13.8 Notifications and timeline

```text
GET    /api/notifications
POST   /api/notifications/{notificationId}/read
POST   /api/notifications/read-all
GET    /api/properties/{propertyId}/timeline
```

### 13.9 Reports and handover

```text
POST   /api/inspections/{inspectionId}/reports
GET    /api/reports/{reportId}
GET    /api/reports/{reportId}/download
POST   /api/reports/{reportId}/acknowledgements
GET    /api/tenancies/{tenancyId}/handover
POST   /api/tenancies/{tenancyId}/handover
PATCH  /api/handover/{handoverId}
POST   /api/handover/{handoverId}/acknowledgements
```

### 13.10 Deposits, utilities, and inventory

```text
GET    /api/tenancies/{tenancyId}/deposit
POST   /api/tenancies/{tenancyId}/deposit
POST   /api/deposit/{depositId}/deductions
POST   /api/deductions/{deductionId}/responses
GET    /api/properties/{propertyId}/utility-readings
POST   /api/properties/{propertyId}/utility-readings
GET    /api/properties/{propertyId}/inventory
POST   /api/properties/{propertyId}/inventory
PATCH  /api/inventory/{inventoryItemId}
POST   /api/inventory/{inventoryItemId}/observations
```

### 13.11 Offline synchronization

```text
POST   /api/sync/batch
GET    /api/sync/status/{clientOperationId}
```

### 13.12 API conventions

- Use consistent JSON request/response objects.
- Return appropriate HTTP status codes and a predictable error structure.
- Validate all incoming values on the backend.
- Use pagination for message history, timelines, properties, and large lists.
- Use server timestamps for authoritative events.
- Use idempotency/client-operation identifiers for retryable create/upload/sync operations.
- Never accept a property/user ID from the client as proof of permission.
- Do not return raw password hashes, invitation tokens after their one-time presentation, private storage paths, or internal security secrets.

---

## 14. Offline operation and synchronization

### 14.1 Offline scope

Offline support is primarily for inspection work. The first offline implementation should support:
- Opening inspection data previously downloaded to the device.
- Editing room checklist condition and notes.
- Capturing/attaching photographs locally.
- Saving the draft and marking it Pending Sync.
- Uploading the queued changes when connectivity returns.

Live chat, invitations, membership changes, and final server-side approvals should require connectivity in the initial release unless explicitly designed otherwise.

### 14.2 Synchronization states

| State | Meaning |
|---|---|
| Local only | Saved on the device but not yet sent to the server. |
| Pending upload | Waiting for a network connection or upload slot. |
| Syncing | Upload/request is currently being processed. |
| Synced | Server accepted the operation and returned a confirmed record/reference. |
| Failed | Operation could not be completed; user can inspect the error and retry. |
| Conflict | Server and local versions both changed and require resolution. |

### 14.3 Synchronization process

1. Each locally created operation receives a stable client operation ID.
2. The local queue stores the operation type, local record ID, payload/reference, and retry state.
3. When connectivity is available, the client sends queued operations in dependency order (for example, create inspection before uploading its evidence).
4. The backend authenticates the user and re-checks property membership.
5. The backend uses the operation ID/idempotency key to prevent duplicate creation after a retry.
6. Successful operations return server IDs and version information.
7. The client updates local references and marks those operations Synced.
8. Failed operations remain visible and can be retried.
9. Conflicting data is not silently overwritten; the app displays the conflicting fields and allows an authorized user to choose or merge values where safe.

### 14.4 Conflict rules

- Append-only records such as comments and messages should use unique client IDs and deduplication rather than overwrite semantics.
- Checklist field edits should compare the server version with the version originally downloaded.
- Evidence files should be uploaded as new evidence/version records; do not silently replace an original file.
- Membership revocation must be enforced server-side even if the device still contains cached data.
- Local cached private information should be protected by device storage controls and cleared or restricted after logout according to the app's privacy design.

---

## 15. Security, privacy, and evidence integrity

### 15.1 Authentication

- Use Spring Security and a mobile-compatible session/token approach.
- Use a suitable adaptive one-way password hash.
- Protect refresh credentials and store mobile tokens using secure platform storage.
- Apply HTTPS for deployed environments.
- Support logout and token/session revocation where applicable.
- Never place credentials, private keys, or long-lived tokens in source code.

### 15.2 Authorization

- Enforce property membership on every private API request.
- Check conversation membership for every chat request.
- Check inspection/maintenance/report linkage before serving attachments.
- Restrict tenant invitation tokens to one intended property and an expiry period.
- Re-check access at synchronization time.
- Test cross-property access using a user who is not a member.

### 15.3 File and attachment security

- Store uploaded files outside publicly served static folders.
- Generate server-side storage names rather than trusting user-supplied paths.
- Validate actual file type, size, and permitted formats.
- Prevent path traversal and unauthorized file retrieval.
- Apply authorization before returning a private file or report.
- Consider malware scanning and image metadata handling before production deployment.

### 15.4 Evidence integrity

For each original evidence file, ProofNest should record:
- file identifier and protected storage reference;
- uploader/user ID;
- server upload timestamp;
- relevant property, inspection, room, and checklist item;
- original filename and detected MIME type;
- file size;
- SHA-256 hash of the stored file bytes;
- version and replacement history where applicable.

A verification operation recalculates the hash from the stored bytes and compares it with the recorded hash. A mismatch indicates that the bytes differ from the hashed version. A matching hash does not prove who took the photograph, when or where it was captured, or whether the depicted condition is accurate.

### 15.5 Audit trail

Important events should include:
- account and membership changes;
- tenant invitation creation, acceptance, and revocation;
- property and inspection creation/update/submission;
- evidence upload, version creation, and integrity verification;
- finding creation, comment, response, and status change;
- message creation and attachment addition (avoid copying message content into audit metadata unnecessarily);
- maintenance status changes;
- report generation and acknowledgement;
- handover, deposit, inventory, and utility-reading changes;
- offline synchronization acceptance or rejection.

Audit events should contain actor, event type, target record, server timestamp, and safe metadata. Ordinary users should not be able to edit audit history.

### 15.6 Privacy and data minimization

- Collect only information needed for the product workflow.
- Restrict personal information and private conversations to authorized members.
- Avoid storing full identity documents or payment credentials in the initial prototype.
- Define what happens to access when a tenancy ends.
- Document retention/deletion rules with the supervisor before a real-world deployment.
- Protect device-local cached evidence and clear/restrict it after logout or membership revocation as appropriate.

---

## 16. Non-functional requirements

| ID | Category | Requirement / target |
|---|---|---|
| NFR-001 | Security | Passwords must use an appropriate adaptive one-way hash; plain-text passwords must never be stored. |
| NFR-002 | Authorization | Backend APIs must enforce property, tenancy, inspection, and conversation permissions. |
| NFR-003 | Privacy | Private evidence and reports must not be available through unrestricted public URLs. |
| NFR-004 | Integrity | SHA-256 hashes and audit events must support detection/traceability of stored-file changes, with limitations documented. |
| NFR-005 | Usability | Main workflows must use clear labels, understandable states, and consistent navigation. |
| NFR-006 | Mobile compatibility | Initial target is Android; layouts must adapt to common phone sizes and orientations where practical. |
| NFR-007 | Performance | For the academic demonstration dataset and development environment, ordinary API-backed screens should generally load within 3 seconds, excluding large file transfers or network limitations. |
| NFR-008 | Reliability | Failed requests/uploads must show an error and must not be displayed as successfully saved. |
| NFR-009 | Offline reliability | Supported offline changes must survive app restart and remain queued until acknowledged by the server. |
| NFR-010 | Synchronization safety | Retried operations must not create duplicate records; conflicts must not silently overwrite data. |
| NFR-011 | Maintainability | UI, state management, API access, business services, persistence, and file storage should be separated. |
| NFR-012 | Testability | Core business logic, API behavior, and authorization must be testable through automated tests. |
| NFR-013 | Accessibility | Controls must have readable labels, adequate contrast, and usable touch targets; status must not rely on colour alone. |
| NFR-014 | Data consistency | Related records must use validated relationships and database constraints where appropriate. |
| NFR-015 | Time handling | Store authoritative timestamps consistently and display local time clearly. |
| NFR-016 | Scalability of design | File storage and notification delivery should be abstracted so they can be upgraded without rewriting all business logic. |
| NFR-017 | Backup and recovery | The demo deployment must have a documented database/file backup and restore procedure. |
| NFR-018 | Error handling | Network, authentication, validation, file, and synchronization errors must provide actionable user feedback without exposing internal stack traces. |

---

## 17. Reports and outputs

### 17.1 Inspection report

The inspection report should include:
- ProofNest branding and report identifier;
- property name/reference and address;
- tenancy reference where applicable;
- inspection type (move-in or move-out);
- inspection date, creator, and participants;
- rooms and checklist results;
- selected photographs with captions/metadata;
- findings and their current status;
- separate landlord and tenant responses;
- selected comments and supporting evidence references;
- report generation time and version/snapshot identifier;
- a note that the report records information entered by participants and is not a legal determination.

### 17.2 Comparison report

The comparison report may include:
- move-in and move-out inspection references;
- matching rooms/items;
- paired photographs;
- manually created findings;
- participant responses;
- unresolved/disputed items.

### 17.3 Maintenance summary

A maintenance export or report may include:
- request category and description;
- reported date and reporter;
- status history;
- scheduled date if recorded;
- update comments;
- completion photographs/documents.

### 17.4 Handover report

A handover report may include:
- date and participants;
- key/access-card checklist;
- furniture/appliance inventory and condition;
- utility readings with units and photographs;
- original deposit amount for reference;
- proposed deductions and party responses;
- acknowledgement history;
- unresolved items.

### 17.5 Report integrity and acknowledgement

A report acknowledgement records that a user received or reviewed a report. It must not automatically mean that the user agrees with every finding, accepts a deposit deduction, or waives a right. Those actions must remain separate and explicit.

---

## 18. Acceptance criteria and success measures

The prototype will be considered successful when the following core scenarios can be demonstrated:

1. A landlord can register, sign in, create a property, and add rooms.
2. The landlord can invite a tenant to one property.
3. The tenant can accept the invitation and access that property.
4. A non-member cannot access the property's private records or conversations.
5. The landlord and tenant can create or participate in a move-in inspection.
6. A user can complete room checklist items, add notes, and attach photographs.
7. An inspection can be saved as a draft and resumed.
8. The app can create a move-out inspection for the same property/tenancy.
9. Authorized users can view move-in and move-out photographs side by side.
10. A user can create a finding linked to supporting evidence.
11. Landlord and tenant can submit separate responses and comments.
12. Conflicting responses remain visible as disputed/unresolved.
13. Authorized members can exchange property-specific chat messages.
14. Chat messages and attachments are not visible to non-members.
15. A user can submit a maintenance request and follow its status updates.
16. Important property events appear in the property timeline.
17. The application can generate an inspection PDF containing the selected records.
18. Report receipt acknowledgement is distinct from accepting findings.
19. A handover record can capture inventory and meter readings.
20. A proposed deposit deduction can be recorded with a separate tenant response.
21. The system can recalculate an evidence hash and flag a changed file in a controlled test.
22. Important actions produce audit events.
23. Supported offline inspection changes remain queued and synchronize without duplicate records in a controlled test.
24. The application displays clear errors for invalid input, network failure, failed upload, and denied access.

### 18.1 Suggested measurable evaluation indicators

| Indicator | Measurement approach |
|---|---|
| Workflow completion | Percentage of test users able to complete a move-in inspection using the checklist. |
| Evidence association accuracy | Check whether uploaded photos are linked to the intended property, inspection, room, and checklist item. |
| Comparison usability | Ask users whether they can locate and compare corresponding move-in/move-out images. |
| Response traceability | Verify that both parties' responses remain separate and timestamped. |
| Chat correctness | Verify message ordering, sender identity, attachments, and conversation access restrictions. |
| Maintenance tracking | Verify each status transition and update is recorded. |
| Offline synchronization | Test local draft persistence, retry, duplicate prevention, and conflict handling. |
| Access control | Attempt cross-property access using an unauthorized account and verify rejection. |
| Report completeness | Compare report output with the underlying stored records. |
| User satisfaction | Use a short structured usability questionnaire with test participants, subject to supervisor approval. |

---

## 19. Testing and evaluation plan

### 19.1 Unit testing

Test individual business rules and services, including:
- email normalization and duplicate registration handling;
- property membership and permission evaluation;
- invitation expiry and acceptance;
- inspection type and status transitions;
- checklist validation;
- evidence hash calculation and verification;
- finding response aggregation;
- maintenance status transitions;
- deposit amount validation;
- utility reading units and values;
- notification recipient selection;
- synchronization idempotency and conflict detection.

### 19.2 Backend integration testing

Test:
- registration and login API;
- property creation and membership APIs;
- invitation lifecycle;
- inspection and evidence persistence;
- private file upload/download authorization;
- chat message creation and retrieval;
- maintenance request lifecycle;
- report generation and download authorization;
- audit-event creation;
- offline synchronization and duplicate retry protection.

### 19.3 Flutter testing

Test:
- login and registration form validation;
- bottom navigation and property navigation;
- room checklist progress;
- photo selection/capture flows;
- comparison layout;
- finding response controls;
- chat message list and attachment presentation;
- maintenance status display;
- notification read state;
- offline indicators and retry actions;
- accessibility labels and common phone layouts.

### 19.4 Security and authorization testing

At minimum, test these negative cases:
- user A attempts to view user B's unrelated property;
- tenant attempts to revoke membership or modify landlord-only fields;
- user outside a conversation requests its messages;
- user attempts to download evidence using another property's file ID;
- expired or revoked invitation is accepted;
- revoked member attempts to synchronize cached offline changes;
- user attempts to change another participant's response;
- a client retries a create/upload request with the same operation ID.

### 19.5 Usability evaluation

A small number of test participants may be asked to complete realistic scenarios:
1. accept a property invitation;
2. complete a sample room checklist;
3. upload evidence;
4. find and compare two photographs;
5. respond to a finding;
6. send a property message;
7. create and track a maintenance request;
8. review a handover report.

Measure task completion, obvious errors, time taken for selected tasks, and user feedback. Obtain supervisor approval and any required consent before collecting participant information.

---

## 20. Development process and eight-week roadmap

### 20.1 Development approach

Use iterative, vertical-slice development. Each feature should be implemented through the mobile screen, API endpoint, business service, database persistence, authorization, and test before moving to polish. This reduces the risk of creating many attractive screens that do not work end to end.

### 20.2 Eight-week roadmap

| Week | Focus | Main development tasks | Expected deliverable |
|---|---|---|---|
| 1 | Requirements and foundation | Confirm BRD/ERD, inspect existing Spring Boot project, create Flutter project, establish theme, navigation, API configuration, and Git workflow. | Running Flutter shell and agreed data/architecture design. |
| 2 | Authentication and access | Mobile login/register APIs, secure session/token storage, user profile, property memberships, secure invitations. | User can sign in and access an invited property. |
| 3 | Properties and structure | Property CRUD, rooms, tenancy association, inventory base, property detail and timeline foundation. | Landlord can create a property and tenant can view authorized property. |
| 4 | Inspections and evidence | Move-in/move-out inspections, room checklists, camera/gallery, notes, upload, evidence metadata, hash, draft persistence. | Complete inspection capture workflow. |
| 5 | Comparison and joint review | Side-by-side comparison, findings, comments, separate accept/dispute responses, audit history, PDF report foundation. | Core ProofNest inspection and dispute workflow. |
| 6 | Communication and maintenance | Property conversations, message history, attachments, notification records, maintenance requests, status updates. | Users can communicate and track maintenance. |
| 7 | Handover and tenancy records | Timeline refinement, handover, deposit records/responses, utility readings, inventory observations, reports and acknowledgements. | End-of-tenancy record workflow. |
| 8 | Offline, security, and final testing | Offline draft queue, retry/idempotency, essential conflict handling, access-control tests, usability checks, bug fixes, documentation, demo preparation. | Integrated prototype, test results, and final demonstration. |

### 20.3 Realistic delivery note

This is an ambitious scope for one developer in eight weeks, particularly because chat, attachments, secure access, PDF reports, and offline synchronization each require backend and mobile work. The order above is designed to protect the core workflow. If time becomes limited, deliver a simpler but working version of every agreed feature group and document the limits; do not sacrifice authentication, property-level authorization, evidence correctness, or the move-in/move-out comparison workflow to add cosmetic features.

### 20.4 Definition of done for a feature

A feature is not considered complete merely because its screen is designed. It is complete when:
1. the intended user can reach the screen;
2. input validation works;
3. the backend enforces authorization and business rules;
4. data is saved and retrieved correctly;
5. errors are displayed clearly;
6. relevant audit/notification behavior works;
7. a repeatable test covers the main success and failure paths.

---

## 21. Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Scope is too large for the available time | Some workflows may remain incomplete. | Build end-to-end core workflows first; use priority levels and simplified secondary features. |
| Existing web backend is not ready for mobile APIs | Rework may be required. | Inspect existing controllers, services, entities, security, and database before changing architecture. Add REST APIs incrementally. |
| Mobile authentication differs from the current web login flow | Login may work in browser but fail in Flutter. | Design and test token/session authentication specifically for API clients; do not expose browser-only assumptions. |
| Property authorization is incomplete | Private records or chat may be exposed. | Centralize permission checks and test cross-property access on every protected module. |
| Photo uploads are large or unreliable | Slow screens and missing evidence. | Compress/display thumbnails where appropriate, upload originals securely, validate limits, and provide retry state. |
| Offline synchronization creates duplicates | Inspection records or photos may be duplicated. | Stable client operation IDs, idempotent endpoints, dependency ordering, and sync tests. |
| Offline and server edits conflict | One participant's changes may be lost. | Use record versions and explicit conflict review; avoid silent last-write-wins for important evidence. |
| Chat is treated as proof of agreement | Users may misunderstand message/read status. | Clearly separate messages, finding responses, report acknowledgements, and formal agreement records. |
| Evidence hashes are misunderstood | Users may believe a hash proves photo authenticity. | Explain exactly what the hash verifies and what it cannot prove. |
| Disputes remain unresolved | Users may expect the system to decide who is right. | Preserve both positions and show unresolved status; do not make legal or liability decisions. |
| Deposit records are mistaken for payment processing or legal advice | Incorrect expectations. | Label amounts as records/proposals only; no payments or legal entitlement calculations. |
| Notification permission is denied | User may miss push alerts. | Keep an in-app notification centre and show permission status. |
| PDF generation is inconsistent | Reports may omit or misrepresent records. | Generate from a consistent data snapshot and test report contents. |
| Local file storage is exposed | Private photographs/documents may leak. | Store outside public static directories and serve only after backend authorization. |
| Personal data is retained unnecessarily | Privacy risk. | Minimize collected fields and define archive/retention rules before production deployment. |
| External deployment/network setup is unavailable | Remote mobile testing may be blocked. | Test locally first; document emulator/device networking configuration and use a controlled demo environment. |

---

## 22. Assumptions, constraints, and dependencies

### 22.1 Assumptions

1. The first target is Android; iOS is not a required first-release deliverable unless the supervisor changes the scope.
2. Landlords and tenants can create accounts and accept property-specific invitations.
3. One property may have successive tenancies over time.
4. Users have access to a mobile camera or image gallery for evidence capture.
5. Users may have intermittent internet connectivity during inspections.
6. The application is a prototype for academic demonstration and controlled evaluation.
7. A small demonstration dataset and sample properties will be available.
8. The application will not process rental payments or deposit transfers.
9. The project will not include AI inspection analysis.
10. External push-notification delivery may require provider configuration; an in-app notification centre is the fallback.

### 22.2 Constraints

- The project is developed individually.
- The target development period is approximately eight weeks.
- The developer is moving from a Thymeleaf web interface to a Flutter mobile interface.
- The current prototype intentionally has no backend/database. Firebase is a candidate for shared authentication and data, pending selection.
- Available time, testing devices, internet connectivity, and external service costs may affect delivery depth.
- A complete production-grade chat and offline sync system is complex; the academic prototype must define and test its supported behavior precisely.

### 22.3 Dependencies

- Flutter SDK and Dart tooling.
- Android SDK/emulator or Android test device.
- Firebase project, authentication providers, data model, and security rules if Firebase is selected.
- Camera/gallery and file-picker packages compatible with the selected Flutter version.
- Secure local storage for authentication tokens and supported offline data.
- Private file storage for photographs, attachments, and generated reports.
- PDF-generation library/service.
- Optional push-notification provider configuration if remote push is included.

---

## 23. Out of scope

The following are not part of the agreed initial ProofNest mobile project:

1. **AI Inspection Assistant:** no AI damage detection, photo interpretation, or AI-generated finding suggestions.
2. AI-based decision-making or automated responsibility assignment.
3. Rent collection, online payments, bank transfers, or deposit refunds.
4. A property rental marketplace, property search portal, or public listing platform.
5. Automated legal advice, legal liability decisions, or guaranteed dispute resolution.
6. Professional valuation, structural engineering assessment, or certified building inspection.
7. Guaranteed proof of original photo capture time, location, identity, or authenticity.
8. Full legal e-signature infrastructure or identity-verification/KYC service.
9. Integration with government, bank, insurance, or external property-management systems.
10. Contractor marketplace, contractor bidding, or complex vendor procurement in the initial release.
11. Full iOS release as a required graduation deliverable; architecture should not prevent it later.
12. Large-scale production hosting and commercial service-level guarantees unless separately approved.

---

## 24. Open questions for supervisor review

1. Is Android-only delivery acceptable for the graduation project, with iOS as future work?
2. Should Firebase Authentication and a Firebase data store be selected for the shared backend, or should another approach be evaluated?
3. Which token/session approach is preferred or required for Flutter authentication?
4. Is WebSocket real-time chat required, or is persisted REST messaging with periodic refresh sufficient for the first prototype?
5. Which offline operations must be supported in the academic demonstration: checklist/notes only, or photos and full synchronization as well?
6. Is PDF generation a mandatory deliverable?
7. Should the optional property-manager role be implemented, or should the prototype be restricted to landlord and tenant?
8. What is the expected retention and archive behavior after a tenancy ends?
9. Are real users permitted for usability evaluation, and what consent/ethics procedure is required?
10. Is a hosted backend required, or is local network/emulator demonstration acceptable?
11. Are there institutional privacy, data-protection, or software-security requirements that must be reflected in the final implementation?
12. What evaluation method should be used to measure usability and improvement in inspection-record organization?

---

## 25. Glossary

| Term | Definition |
|---|---|
| BRD | Business Requirements Document. |
| Property | A rental unit/building managed within ProofNest. |
| Tenancy | A period during which a tenant is associated with a property. |
| Property membership | A user’s permission-based relationship with a specific property. |
| Landlord | Property owner or authorized owner-side participant. |
| Tenant | Person occupying or renting the property and invited to its records. |
| Inspection | A structured record of a property's condition at a point in time. |
| Move-in inspection | Inspection recording condition at the beginning of a tenancy. |
| Move-out inspection | Inspection recording condition at the end of a tenancy. |
| Room/area | A named part of a property, such as kitchen or bedroom. |
| Checklist item | A specific component or condition to review within a room. |
| Evidence | A photograph, document, or other file associated with a property workflow. |
| Evidence version | A later file version retained without silently replacing the original. |
| SHA-256 hash | A cryptographic digest used to compare file bytes against a previously recorded digest. |
| Finding | A recorded observation or possible difference for participants to review. |
| Dispute | A participant's recorded disagreement with a finding or proposed deduction. |
| Acknowledgement | A record that a user received or reviewed an item; it is not automatically agreement. |
| Conversation | A property-associated message thread with an authorized member list. |
| Maintenance request | A record describing a repair or maintenance issue and its status/history. |
| Property timeline | Chronological view of selected property events. |
| Handover | Process of recording property/items/keys and related information when possession changes. |
| Inventory | List of furniture, appliances, keys, access cards, or other tracked items. |
| Utility reading | Recorded electricity, water, or other meter value with date and unit. |
| Deposit record | Informational record of the original deposit and proposed deductions; not a payment. |
| Offline mode | Ability to create supported local inspection changes without an active connection. |
| Synchronization | Process of sending local offline changes to the future shared data store and resolving their status. |
| Audit trail | Chronological record of important system actions and state changes. |
| REST API | HTTP-based interface between clients and a service; not used by the current prototype. |
| Flutter | UI toolkit used to build the mobile application. |
| Firebase | Candidate future platform for shared authentication and data; not yet configured. |

---

## Document note

This BRD is the updated project-planning baseline for the ProofNest mobile application, based on the requirements agreed during project planning. It should be reviewed with the academic supervisor before being treated as formally approved. The final ERD, API contract, detailed UI designs, and test cases should be updated to remain consistent with this document as implementation progresses.
