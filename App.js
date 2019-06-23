/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TextInput, Button, Alert } from 'react-native';
import axios from 'axios';
import HyperpayBridge from './HyperpayBridge';
import { NativeModules, NativeEventEmitter } from 'react-native';

import { DeviceEventEmitter } from 'react-native';
import { WebView } from 'react-native-webview';

const hyperpay = NativeModules.Hyperpay;

const instructions = Platform.select({
	ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
	android: 'Double tap R on your keyboard to reload,\n' + 'Shake or press menu button for dev menu'
});

export default class App extends Component {
	state = {
		checkoutId: '',
		cardNumber: '4111111111111111',
		cvv: '123',
		expiryMonth: '05',
		expirYear: '2021',
		cardHolder: 'hussamadin',
		txStatus: 'pending'
	};
	componentDidMount() {
		axios({
			method: 'post',
			url: 'http://saib.gate2play.com/hussam/payment.php',
			headers: {},
			data: {
				method: 'payment',
				amount: '100'
			}
		}).then((response) => {
			const checkoutId = response.data.checkoutId;

			this.setState({ checkoutId: checkoutId });
		});

		const nativeEventListener = DeviceEventEmitter.addListener('transactionStatus', (e) => {
			//this.setState({ txStatus: e.status });

			//REdirect to 3dSecure
			<WebView source={{ uri: e.redirectUrl }} />;
			console.log(e.status);
		});
	}

	async _onPressButton() {
		// Alert.alert('checkout', this.state.cardHolder);
		const paymentParams = {
			checkoutID: this.state.checkoutId,
			paymentBrand: 'VISA',
			cardNumber: this.state.cardNumber,
			holderName: this.state.cardHolder,
			expiryMonth: this.state.expiryMonth,
			expiryYear: this.state.expirYear,
			cvv: this.state.cvv
		};
		NativeModules.Hyperpay.transactionPayment(paymentParams);

		//NativeModules.Hyperpay.sayHI();
	}

	render() {
		return (
			<View style={styles.container}>
				<Text style={styles.instructions}>{this.state.checkoutId}</Text>
				<TextInput
					style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
					onChangeText={(text) => this.setState({ cardNumber: text })}
					placeholder="Card Number"
					value={this.state.cardNumber}
				/>
				<TextInput
					style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
					onChangeText={(text) => this.setState({ cvv: text })}
					placeholder="CVV"
					value={this.state.cvv}
				/>
				<TextInput
					style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
					onChangeText={(text) => this.setState({ expiryMonth: text })}
					placeholder="MM"
					value={this.state.expiryMonth}
				/>

				<TextInput
					style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
					onChangeText={(text) => this.setState({ expirYear: text })}
					placeholder="YYYY"
					value={this.state.expirYear}
				/>

				<TextInput
					style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
					onChangeText={(text) => this.setState({ cardHolder: text })}
					placeholder="Card Holder name"
					value={this.state.cardHolder}
				/>

				<Button onPress={this._onPressButton.bind(this)} title="Press Me" />
				<Text style={styles.instructions}>{this.state.txStatus}</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: '#F5FCFF'
	},
	welcome: {
		fontSize: 20,
		textAlign: 'center',
		margin: 10
	},
	instructions: {
		textAlign: 'center',
		color: '#333333',
		marginBottom: 5
	}
});
