import { NativeModules } from 'react-native';

const TranslatePinyin = NativeModules.RNChineseToPinyin;

const toPinyin = (str) => {
  const str = `${str}`;
  return TranslatePinyin.getPinyin(str);
}

const toPinyinSync = (str) => {
  const str = `${str}`;
  return TranslatePinyin.getPinyinSync(str);
}

export {
  toPinyin,
  toPinyinSync,
};
