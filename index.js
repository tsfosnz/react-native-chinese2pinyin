import { NativeModules } from 'react-native';

const TranslatePinyin = NativeModules.RNChineseToPinyin;

const getPinyin = str => TranslatePinyin.getPinyin(`${str}`);

const getPinyinSync = str => TranslatePinyin.getPinyinSync(`${str}`);

export {
  getPinyin,
  getPinyinSync,
};
