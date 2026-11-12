import 'package:flutter/material.dart';

import '../core/api_exception.dart';
import 'common.dart';

/// Loads data once, shows a spinner / error with retry, and reloads when [refreshKey] changes.
/// While reloading it keeps showing the previous data, so lists do not flash.
class LoadingBuilder<T> extends StatefulWidget {
  const LoadingBuilder({
    super.key,
    required this.loader,
    required this.builder,
    this.refreshKey,
    this.frame,
  });

  final Future<T> Function() loader;

  /// Receives the loaded data and a function that reloads it (for pull-to-refresh).
  final Widget Function(
      BuildContext context, T data, Future<void> Function() reload) builder;

  /// When this value changes, the data is loaded again.
  final Object? refreshKey;

  /// Wraps the loading and error states (for example in a Scaffold with a back button) for full-screen pages.
  /// Not needed when the loader sits inside a Scaffold that already exists.
  final Widget Function(Widget body)? frame;

  @override
  State<LoadingBuilder<T>> createState() => _LoadingBuilderState<T>();
}

class _LoadingBuilderState<T> extends State<LoadingBuilder<T>> {
  T? _data;
  bool _hasData = false;
  bool _loading = true;
  Object? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  @override
  void didUpdateWidget(covariant LoadingBuilder<T> oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.refreshKey != widget.refreshKey) {
      _load(silent: true);
    }
  }

  Future<void> _load({bool silent = false}) async {
    if (!silent && mounted && _hasData == false) {
      setState(() {
        _loading = true;
        _error = null;
      });
    }
    try {
      final data = await widget.loader();
      if (!mounted) {
        return;
      }
      setState(() {
        _data = data;
        _hasData = true;
        _loading = false;
        _error = null;
      });
    } catch (e) {
      if (!mounted) {
        return;
      }
      setState(() {
        _loading = false;
        // Keep older data on screen if a refresh fails; show the error only when there is nothing to show.
        if (!_hasData) {
          _error = e;
        }
      });
    }
  }

  Future<void> _reload() => _load(silent: true);

  @override
  Widget build(BuildContext context) {
    if (_hasData) {
      return widget.builder(context, _data as T, _reload);
    }
    final Widget body = _loading
        ? const Center(child: CircularProgressIndicator())
        : ErrorState(message: errorText(_error ?? 'error'), onRetry: _load);
    final frame = widget.frame;
    return frame == null ? body : frame(body);
  }
}
