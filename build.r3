;;   ____  __   __        ______        __
;;  / __ \/ /__/ /__ ___ /_  __/__ ____/ /
;; / /_/ / / _  / -_|_-<_ / / / -_) __/ _ \
;; \____/_/\_,_/\__/___(@)_/  \__/\__/_// /
;;  ~~~ oldes.huhuman at gmail.com ~~~ /_/
;;
;; SPDX-License-Identifier: Apache-2.0

Rebol [
	title:   "Build System.ane"
	purpose: "Build System AIR native extension"
	needs: 3.16.0 ;; https://github.com/Oldes/Rebol3/releases/tag/3.16.0
]

import airsdk ;== https://github.com/Oldes/Rebol-AIRSDK

make-dir %build/

air-task
"Compile SystemExtension SWC" [
	compc [
		-swf-version     33
		-source-path     %platform/actionscript/src
		-include-classes %tech.oldes.system.SystemExtension
		-output          %build/tech.oldes.system.swc
	]
]

air-task
"Compile Android natives" [
	cd %platform/android
	eval %gradlew [clean build]
	print as-green "Lint results:"
	try [print read/string %app/build/reports/lint-results-debug.txt]
	cd %../..
	copy-file %platform/android/app/build/outputs/aar/app-release.aar %build/tech.oldes.system.aar
]

air-task
"Compile SystemExtension ANE" [
	delete-file %build/tech.oldes.system.ane
	build-ane [
		id:  @tech.oldes.system
		initializer: @SystemExtension
		platforms: [Android-ARM Android-ARM64 Android-x86 Android-x64]
	]
]

air-task
"Copy ANE to test app folder" [
	copy-file %build/tech.oldes.system.ane %\c\Dev\Builder\tree\air\HelloAir\Extensions\tech.oldes.system.ane
]