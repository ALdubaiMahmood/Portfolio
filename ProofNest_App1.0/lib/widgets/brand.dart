import 'package:flutter/material.dart';

import '../core/theme.dart';

/// The ProofNest house-and-check mark, drawn from the same paths as static/images/proofnest-logo.svg.
/// Size is set in ONE place: the [size] (height) parameter.
class BrandMark extends StatelessWidget {
  const BrandMark({super.key, this.size = 40});

  final double size;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size * 50 / 60,
      height: size,
      child: CustomPaint(painter: _BrandMarkPainter()),
    );
  }
}

class _BrandMarkPainter extends CustomPainter {
  // The SVG drawing occupies x 4..54 and y 11..71 (including stroke width).
  static const double _artWidth = 50;
  static const double _artHeight = 60;

  @override
  void paint(Canvas canvas, Size size) {
    final scale = size.width / _artWidth < size.height / _artHeight
        ? size.width / _artWidth
        : size.height / _artHeight;
    canvas.save();
    canvas.translate((size.width - _artWidth * scale) / 2,
        (size.height - _artHeight * scale) / 2);
    canvas.scale(scale);
    canvas.translate(-4, -11);

    final shader = const LinearGradient(
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      colors: [Color(0xFFFF2449), Color(0xFF8B0D1A)],
    ).createShader(const Rect.fromLTWH(4, 11, _artWidth, _artHeight));

    final house = Paint()
      ..shader = shader
      ..style = PaintingStyle.stroke
      ..strokeWidth = 6
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round;
    final check = Paint()
      ..shader = shader
      ..style = PaintingStyle.stroke
      ..strokeWidth = 5
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round;

    // M7 33 29 14 51 33 V68 H35 V49 H23 V68 H7 Z
    final housePath = Path()
      ..moveTo(7, 33)
      ..lineTo(29, 14)
      ..lineTo(51, 33)
      ..lineTo(51, 68)
      ..lineTo(35, 68)
      ..lineTo(35, 49)
      ..lineTo(23, 49)
      ..lineTo(23, 68)
      ..lineTo(7, 68)
      ..close();
    // m20 40 7 7 13-15
    final checkPath = Path()
      ..moveTo(20, 40)
      ..lineTo(27, 47)
      ..lineTo(40, 32);

    canvas.drawPath(housePath, house);
    canvas.drawPath(checkPath, check);
    canvas.restore();
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

/// Mark plus the "ProofNest" wordmark ("Nest" in red), like the website header.
class BrandLogo extends StatelessWidget {
  const BrandLogo({super.key, this.height = 40, this.showName = true});

  /// Height of the mark; the wordmark scales with it.
  final double height;
  final bool showName;

  @override
  Widget build(BuildContext context) {
    final fontSize = height * 0.52;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        BrandMark(size: height),
        if (showName) ...[
          SizedBox(width: height * 0.26),
          Text.rich(
            TextSpan(
              style: TextStyle(
                fontSize: fontSize,
                fontWeight: FontWeight.w800,
                letterSpacing: -0.5,
                color: AppColors.text,
              ),
              children: const [
                TextSpan(text: 'Proof'),
                TextSpan(text: 'Nest', style: TextStyle(color: AppColors.red)),
              ],
            ),
          ),
        ],
      ],
    );
  }
}
