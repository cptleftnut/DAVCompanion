import React, { useState, useRef } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ScrollView, KeyboardAvoidingView, Platform, Animated } from 'react-native';

// Sæt denne til din computers IP (f.eks. 'http://192.168.1.X:3000') hvis du tester på fysisk telefon!
const BACKEND_URL = 'http://10.0.2.2:3000'; // 10.0.2.2 virker for Android emulator

export default function App() {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [face, setFace] = useState('( ^ ◡ ^ )');

  const bounceValue = useRef(new Animated.Value(0)).current;

  const bounce = () => {
    Animated.sequence([
      Animated.timing(bounceValue, { toValue: -10, duration: 150, useNativeDriver: true }),
      Animated.timing(bounceValue, { toValue: 0, duration: 150, useNativeDriver: true })
    ]).start();
  };

  const sendMessage = async () => {
    if (!inputText.trim()) return;

    const userMsg = { role: 'user', content: inputText };
    setMessages(prev => [...prev, userMsg]);
    setInputText('');
    setIsLoading(true);
    setFace('( •_• )');

    try {
      const response = await fetch(`${BACKEND_URL}/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: userMsg.content }),
      });
      
      const data = await response.json();
      
      setMessages(prev => [...prev, { role: 'dav', content: data.reply }]);
      setFace('( ^ ▽ ^ )');
      bounce();

    } catch (error) {
      console.error(error);
      setFace('( > _ < )');
      setMessages(prev => [...prev, { role: 'dav', content: '*Forbindelsesfejl.*' }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.container} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <View style={styles.tamagotchiContainer}>
        <Animated.Text style={[styles.face, { transform: [{ translateY: bounceValue }] }]}>
          {face}
        </Animated.Text>
        <Text style={styles.status}>{isLoading ? "DAV tænker..." : "DAV er klar"}</Text>
      </View>

      <ScrollView style={styles.chatContainer} contentContainerStyle={{ padding: 20 }}>
        {messages.map((msg, index) => (
          <View key={index} style={msg.role === 'user' ? styles.userMessage : styles.davMessage}>
            <Text style={msg.role === 'user' ? styles.userText : styles.davText}>{msg.content}</Text>
          </View>
        ))}
      </ScrollView>

      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          value={inputText}
          onChangeText={setInputText}
          placeholder="Snak med DAV..."
          placeholderTextColor="#888"
          editable={!isLoading}
        />
        <TouchableOpacity style={styles.button} onPress={sendMessage} disabled={isLoading}>
          <Text style={styles.buttonText}>Send</Text>
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#1E1E1E' },
  tamagotchiContainer: { height: 250, backgroundColor: '#2A2A2A', justifyContent: 'center', alignItems: 'center', borderBottomWidth: 3, borderBottomColor: '#FF6B6B', paddingTop: 50 },
  face: { fontSize: 50, color: '#FFD93D', fontWeight: 'bold' },
  status: { color: '#888', marginTop: 10, fontSize: 14 },
  chatContainer: { flex: 1 },
  userMessage: { alignSelf: 'flex-end', backgroundColor: '#FF6B6B', padding: 12, borderRadius: 20, marginBottom: 10, maxWidth: '80%' },
  davMessage: { alignSelf: 'flex-start', backgroundColor: '#333333', padding: 12, borderRadius: 20, marginBottom: 10, maxWidth: '80%' },
  userText: { color: '#FFF', fontSize: 16 },
  davText: { color: '#FFF', fontSize: 16, lineHeight: 24 },
  inputContainer: { flexDirection: 'row', padding: 15, backgroundColor: '#2A2A2A' },
  input: { flex: 1, backgroundColor: '#1E1E1E', color: '#FFF', borderRadius: 25, paddingHorizontal: 20, paddingVertical: 12, fontSize: 16 },
  button: { backgroundColor: '#FF6B6B', borderRadius: 25, paddingHorizontal: 20, justifyContent: 'center', marginLeft: 10 },
  buttonText: { color: '#FFF', fontWeight: 'bold', fontSize: 16 }
});
