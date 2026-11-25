"""Check translation coverage, locale registration and format argument safety."""
from collections import Counter
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'shared/src/commonMain/composeResources'
ANDROID_NS = '{http://schemas.android.com/apk/res/android}'


def read_strings(path):
    entries = list(ET.parse(path).getroot())
    names = [entry.attrib['name'] for entry in entries]
    assert len(names) == len(set(names)), f'Duplicate resource names: {path}'
    return {entry.attrib['name']: entry.text or '' for entry in entries}


def verify():
    base = read_strings(RES / 'values/strings.xml')
    config = ET.parse(ROOT / 'androidApp/src/main/res/xml/locales_config.xml')
    locales = [entry.attrib[ANDROID_NS + 'name'] for entry in config.getroot()]
    assert len(locales) == len(set(locales)), 'Duplicate Android locales'
    picker = ROOT / 'shared/src/commonMain/kotlin/com/fansim/app/presentation/language/LanguageScreen.kt'
    picker_tags = re.findall(r'^\s*"([a-zA-Z-]+)" to "', picker.read_text(encoding='utf-8'), re.MULTILINE)
    assert sorted(picker_tags) == sorted(locales), 'Language picker and Android locale config differ'
    assert set('en zh hi es fr ar pt bn ru ur'.split()) <= set(locales)
    placeholder = re.compile(r'%(?:\d+\$)?[ds]')
    for locale in locales:
        if locale == 'en':
            continue
        qualifier = locale.replace('-', '-r')
        path = RES / f'values-{qualifier}/strings.xml'
        translated = read_strings(path)
        # The brand name may intentionally inherit from the default resources.
        if not locale.startswith('en-'):
            assert set(base) - {'app_name'} <= set(translated), f'Missing strings: {locale}'
        assert set(translated) <= set(base), f'Unknown strings: {locale}'
        for key, value in translated.items():
            assert value.strip(), f'Empty translation: {locale}/{key}'
            assert Counter(placeholder.findall(value)) == Counter(placeholder.findall(base[key])), (
                f'Formatting argument mismatch: {locale}/{key}'
            )
    manifest = ET.parse(ROOT / 'androidApp/src/main/AndroidManifest.xml')
    application = manifest.getroot().find('application')
    assert application.attrib[ANDROID_NS + 'supportsRtl'] == 'true'
    assert application.attrib[ANDROID_NS + 'localeConfig'] == '@xml/locales_config'
    print(f'Validated {len(base)} strings across {len(locales)} locales; RTL and locale config enabled.')


if __name__ == '__main__':
    verify()
